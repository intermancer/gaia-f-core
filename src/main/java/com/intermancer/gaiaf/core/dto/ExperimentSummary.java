package com.intermancer.gaiaf.core.dto;

import com.intermancer.gaiaf.core.experiment.ExperimentState;

import java.time.Instant;

/**
 * Data Transfer Object for experiment list display.
 * Provides summary information about an experiment including its ID,
 * creation timestamp, current status, and total cycles completed.
 */
public record ExperimentSummary(
    String id,
    Instant createdAt,
    ExperimentState status,
    int cyclesCompleted
) {}
