package io.chrispysz.model;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class PredictionRequest {

    @NotNull
    private List<String> sequences;

    private String model;

    public List<String> getSequences() {
        return sequences;
    }

    public String getModel() {
        return model;
    }
}
