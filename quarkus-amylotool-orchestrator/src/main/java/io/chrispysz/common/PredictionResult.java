package io.chrispysz.common;

public class PredictionResult {

    private String taskGuid;

    private String identifier;

    private int maxIndex;

    private float score;

    private Integer withModel;

    private boolean isLast = false;


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

    public String getTaskGuid() {
        return taskGuid;
    }

    public void setTaskGuid(String taskGuid) {
        this.taskGuid = taskGuid;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getMaxIndex() {
        return maxIndex;
    }

    public void setMaxIndex(int maxIndex) {
        this.maxIndex = maxIndex;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public Integer getWithModel() {
        return withModel;
    }

    public void setWithModel(Integer withModel) {
        this.withModel = withModel;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }
}
