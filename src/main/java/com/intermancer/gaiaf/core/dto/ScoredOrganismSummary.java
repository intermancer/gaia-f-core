package com.intermancer.gaiaf.core.dto;

/**
 * Data Transfer Object for scored organism list display.
 * Provides summary information about a scored organism including its ID
 * and score, without the full Organism object to reduce response payload size.
 */
public record ScoredOrganismSummary(
    String id,
    Double score
) {}
