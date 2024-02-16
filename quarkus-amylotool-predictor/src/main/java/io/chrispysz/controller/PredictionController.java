package io.chrispysz.controller;

import io.chrispysz.model.PredictionRequest;
import io.chrispysz.service.PredictionService;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/api/prediction")
@Tag(name = "predictions")
public class PredictionController {

    Logger logger;
    PredictionService predictionService;

    public PredictionController(Logger logger, PredictionService predictionService) {
        this.logger = logger;
        this.predictionService = predictionService;
    }

    @Operation(summary = "Run predictions on sequences")
    @POST
    public RestResponse<Void> test(@Valid PredictionRequest request){
        predictionService.predictWithAllModels(request.getSequences().toArray(String[]::new));
        return RestResponse.accepted();
    }
}
