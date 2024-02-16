package io.chrispysz.controller;

import io.chrispysz.entity.Token;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.openapi.quarkus.qa_predictor_yaml.api.PredictionsApi;
import org.openapi.quarkus.qa_predictor_yaml.model.PredictionRequest;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/prediction")
@Tag(name = "prediction")
public class PredictionController {

    Logger log;

    PredictionsApi predictionsApi;

    public PredictionController(Logger log, @RestClient PredictionsApi predictionsApi) {
        this.log = log;
        this.predictionsApi = predictionsApi;
    }

    @Operation(summary = "Run predictions on sequences")
    @POST
    @APIResponse(responseCode = "202", description = "Request accepted by prediction service for further processing")
    @APIResponse(responseCode = "400", description = "Request was not accepted by prediction service")
    @APIResponse(responseCode = "500", description = "Exception occurred when trying to reach prediction service")
    @Consumes(APPLICATION_JSON)
    public Response predict(@Valid PredictionRequest request) {
        log.info("Running predictions on " + request.getSequences().size() + " sequences...");

        try (Response response = predictionsApi.apiPredictionPost(request)) {
            if (response.getStatus() == 202) {
                return Response.accepted().build();
            } else {
                log.warn("Request could not be processed");
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        } catch (Exception e) {
            log.error("Exception occurred when trying to reach prediction service: " + e.getMessage());
            return Response.serverError().build();
        }
    }
}
