package io.chrispysz.repository;

import io.chrispysz.entity.PredictionTask;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PredictionTaskRepository implements PanacheRepositoryBase<PredictionTask, String> {
}
