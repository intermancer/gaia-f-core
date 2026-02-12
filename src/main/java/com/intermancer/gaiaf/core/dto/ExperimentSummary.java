package com.intermancer.gaiaf.core.dto;

import com.intermancer.gaiaf.core.experiment.ExperimentState;

import java.time.Instant;

/**
 * Data Transfer Object for experiment list display.
 * Provides summary information about an experiment including its ID,
 * creation timestamp, and current status.
 */
public record ExperimentSummary(
    String id,
    Instant createdAt,
    ExperimentState status
) {}
