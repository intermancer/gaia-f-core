package com.intermancer.gaiaf.core.experiment.repo;

import com.intermancer.gaiaf.core.experiment.Experiment;
import java.util.List;
import java.util.Optional;

public interface ExperimentRepository {
    Experiment save(Experiment experiment);
    Optional<Experiment> findById(String id);
    List<Experiment> findAll();
    void delete(String id);
    boolean exists(String id);
}