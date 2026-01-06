package com.intermancer.gaiaf.core.experiment.repo;

import com.intermancer.gaiaf.core.experiment.ExperimentStatus;
import java.util.List;
import java.util.Optional;

public interface ExperimentStatusRepository {
    ExperimentStatus save(ExperimentStatus status);
    Optional<ExperimentStatus> findById(String id);
    Optional<ExperimentStatus> findByExperimentId(String experimentId);
    List<ExperimentStatus> findAll();
    void delete(String id);
}