package io.chrispysz.controller;

import io.chrispysz.model.PredictionRequest;
import io.chrispysz.model.PredictionResult;
import io.chrispysz.service.PredictionService;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import java.util.List;

public class PredictionController {

    @Inject
    Logger log;

    @Inject
    PredictionService predictionService;

    @Incoming("requests")
    public void acceptMessage(JsonObject request) {
        PredictionRequest mappedRequest = request.mapTo(PredictionRequest.class);

        List<PredictionResult> results = predictionService.processPredictionRequest(mappedRequest.getSequences(), mappedRequest.getModel(), mappedRequest.getTaskGuid());

        log.info("Finished running " + results.size() + " predictions for id: " + mappedRequest.getTaskGuid());

    }
}
