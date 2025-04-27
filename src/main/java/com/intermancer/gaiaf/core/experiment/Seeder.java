package com.intermancer.gaiaf.core.experiment;

/**
 * Interface for seeders that initialize an OrganismRepository with a set of seed Organisms.
 */
public interface Seeder {
    /**
     * Seeds the OrganismRepository with a set of Organisms.
     */
    void seed();
}
