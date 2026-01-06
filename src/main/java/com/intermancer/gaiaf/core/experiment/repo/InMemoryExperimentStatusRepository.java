package com.intermancer.gaiaf.core.experiment.repo;

import com.intermancer.gaiaf.core.experiment.ExperimentStatus;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of the ExperimentStatusRepository interface.
 * Maintains both ID-based and experiment-ID-based lookups.
 */
@Repository
public class InMemoryExperimentStatusRepository implements ExperimentStatusRepository {

    private final Map<String, ExperimentStatus> statusMap = new ConcurrentHashMap<>();
    private final Map<String, String> experimentIdToStatusIdMap = new ConcurrentHashMap<>();

    @Override
    public ExperimentStatus save(ExperimentStatus status) {
        if (status.getId() == null || status.getId().isEmpty()) {
            throw new IllegalArgumentException("ExperimentStatus ID cannot be null or empty");
        }
        if (status.getExperimentId() == null || status.getExperimentId().isEmpty()) {
            throw new IllegalArgumentException("ExperimentStatus experimentId cannot be null or empty");
        }

        statusMap.put(status.getId(), status);
        experimentIdToStatusIdMap.put(status.getExperimentId(), status.getId());
        return status;
    }

    @Override
    public Optional<ExperimentStatus> findById(String id) {
        return Optional.ofNullable(statusMap.get(id));
    }

    @Override
    public Optional<ExperimentStatus> findByExperimentId(String experimentId) {
        String statusId = experimentIdToStatusIdMap.get(experimentId);
        return statusId != null ? Optional.ofNullable(statusMap.get(statusId)) : Optional.empty();
    }

    @Override
    public List<ExperimentStatus> findAll() {
        return new ArrayList<>(statusMap.values());
    }

    @Override
    public void delete(String id) {
        ExperimentStatus status = statusMap.get(id);
        if (status == null) {
            throw new IllegalArgumentException("ExperimentStatus with ID " + id + " not found");
        }

        statusMap.remove(id);
        experimentIdToStatusIdMap.remove(status.getExperimentId());
    }
}