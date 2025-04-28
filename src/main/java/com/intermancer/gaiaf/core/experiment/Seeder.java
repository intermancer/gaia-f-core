package com.intermancer.gaiaf.core.experiment;

import com.intermancer.gaiaf.core.organism.repo.OrganismRepository;

/**
 * Interface for seeders that initialize an OrganismRepository with a set of seed Organisms.
 */
public interface Seeder {
    /**
     * Seeds the OrganismRepository with a set of Organisms.
     * @param repo The OrganismRepository to seed
     */
    void seed(OrganismRepository repo);
}