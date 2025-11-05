package com.intermancer.gaiaf.core.experiment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * BasicExperimentImpl orchestrates the complete experimentation process.
 * It manages seeding the ScoredOrganismRepository with initial evaluated organisms
 * and executing multiple Experiment Cycles.
 */
@Component
public class BasicExperimentImpl implements Experiment {
    
    @Autowired
    private Seeder seeder;
    
    @Autowired
    private ExperimentConfiguration experimentConfiguration;
    
    @Autowired
    private ExperimentCycle experimentCycle;
    
    /**
     * Executes the complete experiment process:
     * 1. Seeds the ScoredOrganismRepository by calling the Seeder 
     *    (the Seeder evaluates organisms and stores them)
     * 2. Runs the number of experiment cycles specified in ExperimentConfiguration
     */
    @Override
    public void runExperiment() {
        // Step 1: Seed the repository with initial evaluated organisms
        seeder.seed();
        
        // Step 2: Run the configured number of experiment cycles
        int cycleCount = experimentConfiguration.getCycleCount();
        for (int i = 0; i < cycleCount; i++) {
            experimentCycle.mutationCycle();
        }
    }
}
