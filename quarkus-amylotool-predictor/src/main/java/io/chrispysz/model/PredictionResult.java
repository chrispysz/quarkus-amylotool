package io.chrispysz.model;

public class PredictionResult {

    public String taskGuid;

    public String identifier;

    public int maxIndex;

    public float score;

    public Integer withModel;

    public boolean isLast = false;


    public PredictionResult(String taskGuid, String identifier, int maxIndex, float score, Integer withModel) {
        this.taskGuid = taskGuid;
        this.identifier = identifier;
        this.maxIndex = maxIndex;
        this.score = score;
        this.withModel = withModel;
    }

    public PredictionResult() {

    }

    @Override
    public String toString() {
        return "PredictionResult{" +
                "taskGuid=" + taskGuid +
                ", identifier='" + identifier + '\'' +
                ", maxIndex=" + maxIndex +
                ", score=" + score +
                ", withModel=" + withModel +
                '}';

    }
}
