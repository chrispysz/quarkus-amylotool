package io.chrispysz.service;

import io.chrispysz.common.PredictionRequest;
import io.chrispysz.entity.PredictionTask;
import io.chrispysz.entity.SequencePrediction;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PredictionService {


    public PredictionTask mapRequestToTask(PredictionRequest request) {
        PredictionTask task = new PredictionTask();

        List<SequencePrediction> sequencePredictions = new ArrayList<>();
        for (Map.Entry<String, String> entry : request.getSequences().entrySet()) {
            SequencePrediction sequencePrediction = new SequencePrediction();
            sequencePrediction.identifier = entry.getKey();
            sequencePrediction.predictionTask = task;

            sequencePredictions.add(sequencePrediction);
        }

        task.sequencePredictions = sequencePredictions;

        return task;
    }
}
