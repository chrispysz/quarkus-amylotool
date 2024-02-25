package io.chrispysz.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "qao_sequence")
public class SequencePrediction extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String guid;
    public String identifier;

    public Integer maxIndex;

    public float predictionValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qao_prediction_task_id")
    public PredictionTask predictionTask;

    public String getGuid() {
        return guid;
    }
}
