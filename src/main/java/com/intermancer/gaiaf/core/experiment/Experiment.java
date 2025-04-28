package com.intermancer.gaiaf.core.experiment;

import com.intermancer.gaiaf.core.organism.repo.OrganismRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Experiment class implements the Experiment Life Cycle phases, mostly by delegating to injected objects.
 */
@Component
public class Experiment {
    
    private final Seeder seeder;
    private final OrganismRepository organismRepository;
    
    @Autowired
    public Experiment(Seeder seeder, OrganismRepository organismRepository) {
        this.seeder = seeder;
        this.organismRepository = organismRepository;
    }
    
    /**
     * Calls the seed() method of the injected Seeder, passing in the injected OrganismRepository.
     */
    public void seed() {
        seeder.seed(organismRepository);
    }
}