package io.chrispysz.model;

import java.util.Map;

public class PredictionRequest {

    private String taskGuid;

    private Map<String, String> sequences;

    private String model;


    public PredictionRequest(String taskGuid, Map<String, String> sequences, String model) {
        this.taskGuid = taskGuid;
        this.sequences = sequences;
        this.model = model;
    }

    public String getTaskGuid() {
        return taskGuid;
    }

    public Map<String, String> getSequences() {
        return sequences;
    }

    public String getModel() {
        return model;
    }
}
