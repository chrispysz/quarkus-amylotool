package io.chrispysz.controller;

import io.chrispysz.common.PredictionRequest;
import io.chrispysz.common.PredictionResult;
import io.chrispysz.entity.PredictionTask;
import io.chrispysz.entity.SequencePrediction;
import io.chrispysz.entity.enums.PredictionStatus;
import io.chrispysz.repository.PredictionTaskRepository;
import io.chrispysz.service.PredictionService;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("/prediction")
@Tag(name = "prediction")
public class PredictionController {

    @Channel("prediction-requests")
    Emitter<PredictionRequest> predictionRequestsEmitter;

    @Inject
    Logger log;

    @Inject
    PredictionTaskRepository predictionTaskRepository;

    @Inject
    PredictionService predictionService;

    @Operation(summary = "Run predictions on sequences")
    @POST
    @APIResponse(responseCode = "202", description = "Request accepted by prediction service for further processing")
    @APIResponse(responseCode = "400", description = "Request was not accepted by prediction service")
    @APIResponse(responseCode = "500", description = "Exception occurred when trying to reach prediction service")
    @Consumes(APPLICATION_JSON)
    @Produces(TEXT_PLAIN)
    @Transactional
    public Response predict(PredictionRequest request) {
        PredictionTask task = predictionService.mapRequestToTask(request);
        predictionTaskRepository.persist(task);
        request.setTaskGuid(task.getGuid());
        task.status = PredictionStatus.PROGRESSING;

        log.info("New task created. Forwarding request to prediction service...");
        predictionRequestsEmitter.send(request);

        return Response.accepted(task.getGuid()).build();
    }

    @Incoming("results")
    @Transactional
    public void process(JsonObject result) {
        PredictionResult mappedResult = result.mapTo(PredictionResult.class);
        if (mappedResult.getIdentifier() == null || mappedResult.getTaskGuid() == null) {
            log.error("No identifying data was passed, ignoring message!");
            return;
        }

        PredictionTask task = predictionTaskRepository.findById(mappedResult.getTaskGuid());

        SequencePrediction sequencePrediction = new SequencePrediction();
        sequencePrediction.predictionValue = mappedResult.getScore();
        sequencePrediction.maxIndex = mappedResult.getMaxIndex();
        sequencePrediction.identifier = mappedResult.getIdentifier();
        sequencePrediction.predictionTask = task;

        task.sequencePredictions.add(sequencePrediction);

        if (mappedResult.isLast()) {
            task.status = PredictionStatus.COMPLETED;
        }

        log.info("New prediction saved for task guid: " + task.getGuid() + ", identifier: " + sequencePrediction.identifier +
                ", score: " + sequencePrediction.predictionValue + ", starting at index: " + sequencePrediction.maxIndex);
    }
}
