package io.chrispysz.controller;

import io.chrispysz.entity.PredictionResult;
import io.chrispysz.model.PredictionRequest;
import io.chrispysz.service.PredictionService;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

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
    public RestResponse<List<PredictionResult>> predict(@Valid PredictionRequest request) {
        List<PredictionResult> results = predictionService.processPredictionRequest(request.getSequences(), request.getModel());
        return RestResponse.accepted(results);
    }
}
