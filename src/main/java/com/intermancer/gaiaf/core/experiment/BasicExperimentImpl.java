package com.intermancer.gaiaf.core.experiment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * BasicExperimentImpl orchestrates the complete experimentation process.
 * It manages seeding the ScoredOrganismRepository with initial evaluated organisms
 * and executing multiple Experiment Cycles.
 */
@Component
public class BasicExperimentImpl implements Experiment {
    
    private final String experimentId;
    private final Seeder seeder;
    private final ExperimentConfiguration experimentConfiguration;
    private final ExperimentCycle experimentCycle;
    private final ExperimentStatus experimentStatus;
    
    @Autowired
    public BasicExperimentImpl(Seeder seeder,
                               ExperimentConfiguration experimentConfiguration,
                               ExperimentCycle experimentCycle,
                               ExperimentStatus experimentStatus) {
        this.experimentId = UUID.randomUUID().toString();
        this.seeder = seeder;
        this.experimentConfiguration = experimentConfiguration;
        this.experimentCycle = experimentCycle;
        this.experimentStatus = experimentStatus;
    }
    
    @Override
    public String getId() {
        return experimentId;
    }
    
    /**
     * Executes the complete experiment process:
     * 1. Seeds the ScoredOrganismRepository by calling the Seeder 
     *    (the Seeder evaluates organisms and stores them)
     * 2. Runs the number of experiment cycles specified in ExperimentConfiguration
     */
    @Override
    public void runExperiment() {
        // Reset experiment status
        experimentStatus.reset();
        experimentStatus.setStatus(ExperimentState.RUNNING);
        
        try {
            // Seed the repository with the experiment ID
            seeder.seed(experimentId);
            
            // Run experiment cycles
            int cycleCount = experimentConfiguration.getCycleCount();
            for (int i = 0; i < cycleCount; i++) {
                experimentCycle.mutationCycle(experimentId);
                experimentStatus.incrementCyclesCompleted();
            }
            
            experimentStatus.setStatus(ExperimentState.STOPPED);
        } catch (Exception e) {
            experimentStatus.setStatus(ExperimentState.EXCEPTION);
            throw e;
        }
    }
}
