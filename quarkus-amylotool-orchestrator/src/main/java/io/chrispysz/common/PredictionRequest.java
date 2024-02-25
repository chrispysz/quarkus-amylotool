package io.chrispysz.common;

import java.util.Map;

public class PredictionRequest {

    private String taskGuid;

    private Map<String, String> sequences;

    private String model;


    public Map<String, String> getSequences() {
        return sequences;
    }

    public String getModel() {
        return model;
    }

    public String getTaskGuid() {
        return taskGuid;
    }

    public void setTaskGuid(String taskGuid) {
        this.taskGuid = taskGuid;
    }
}

