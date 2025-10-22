package com.intermancer.gaiaf.core.evaluate;

import java.util.UUID;

import com.intermancer.gaiaf.core.organism.Organism;

/**
 * A record representing an Organism and its evaluation score.
 * Implements Comparable to support binary searches in the ScoredOrganismRepository.
 * Two ScoredOrganisms are compared using their score property.
 */
public record ScoredOrganism(
    String id,
    Double score,
    String organismId,
    Organism organism
) implements Comparable<ScoredOrganism> {

    /**
     * Creates a new ScoredOrganism with a generated UUID as the id.
     * The organism parameter is transient and not stored in the repository.
     *
     * @param score The evaluation score for the organism
     * @param organism The actual organism instance (transient, not stored)
     */
    public ScoredOrganism(Double score, Organism organism) {
        this(UUID.randomUUID().toString(), score, organism.getId(), organism);
    }

    /**
     * Compares this ScoredOrganism with another based on their scores.
     * Implements the Comparable interface to enable sorting and binary searches.
     *
     * @param other The other ScoredOrganism to compare to
     * @return A negative integer, zero, or a positive integer as this score
     *         is less than, equal to, or greater than the specified score
     */
    @Override
    public int compareTo(ScoredOrganism other) {
        return this.score.compareTo(other.score);
    }
}