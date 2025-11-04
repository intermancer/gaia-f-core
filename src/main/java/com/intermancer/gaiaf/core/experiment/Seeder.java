package com.intermancer.gaiaf.core.experiment;

/**
 * Interface for seeders that initialize repositories with evaluated seed Organisms.
 * Implementations should use injected dependencies (repositories and evaluator) rather than parameters.
 */
public interface Seeder {
    /**
     * Seeds the repositories with evaluated Organisms.
     * Implementations should use injected ScoredOrganismRepository, OrganismRepository, 
     * and Evaluator to create, evaluate, and store organisms.
     */
    void seed();
}