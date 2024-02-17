package io.chrispysz.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PredictionResult extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonIgnore
    public String id;

    public String sequence;

    public int maxIndex;

    public float score;

    public Integer withModel;


    public PredictionResult(String sequence, int maxIndex, float score, Integer withModel) {
        this.sequence = sequence;
        this.maxIndex = maxIndex;
        this.score = score;
        this.withModel = withModel;
    }

    public PredictionResult() {

    }
}
