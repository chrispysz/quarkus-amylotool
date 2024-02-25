package io.chrispysz.entity;

import io.chrispysz.entity.enums.PredictionStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
@Table(name = "qao_prediction_task")
public class PredictionTask extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String guid;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    public PredictionStatus status;

    @OneToMany(mappedBy = "predictionTask", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<SequencePrediction> sequencePredictions;

    public String getGuid() {
        return guid;
    }
}
