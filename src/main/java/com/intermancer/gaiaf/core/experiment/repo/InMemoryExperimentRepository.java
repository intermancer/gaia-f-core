package com.intermancer.gaiaf.core.experiment.repo;

import com.intermancer.gaiaf.core.experiment.Experiment;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of the ExperimentRepository interface.
 * Stores experiments in a ConcurrentHashMap for thread-safe operations.
 */
@Repository
public class InMemoryExperimentRepository implements ExperimentRepository {

    private final Map<String, Experiment> experimentMap = new ConcurrentHashMap<>();

    @Override
    public Experiment save(Experiment experiment) {
        if (experiment.getId() == null || experiment.getId().isEmpty()) {
            throw new IllegalArgumentException("Experiment ID cannot be null or empty");
        }
        experimentMap.put(experiment.getId(), experiment);
        return experiment;
    }

    @Override
    public Optional<Experiment> findById(String id) {
        return Optional.ofNullable(experimentMap.get(id));
    }

    @Override
    public List<Experiment> findAll() {
        return new ArrayList<>(experimentMap.values());
    }

    @Override
    public void delete(String id) {
        if (!experimentMap.containsKey(id)) {
            throw new IllegalArgumentException("Experiment with ID " + id + " not found");
        }
        experimentMap.remove(id);
    }

    @Override
    public boolean exists(String id) {
        return experimentMap.containsKey(id);
    }
}