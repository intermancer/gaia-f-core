package com.intermancer.gaiaf.core.experiment;

import com.intermancer.gaiaf.core.experiment.repo.ExperimentStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger logger = LoggerFactory.getLogger(BasicExperimentImpl.class);
    private final String experimentId;
    private final Seeder seeder;
    private final ExperimentConfiguration experimentConfiguration;
    private final ExperimentCycle experimentCycle;
    private final ExperimentStatusRepository experimentStatusRepository;
    private ExperimentStatus experimentStatus;
    
    @Autowired
    public BasicExperimentImpl(Seeder seeder,
                               ExperimentConfiguration experimentConfiguration,
                               ExperimentCycle experimentCycle,
                               ExperimentStatusRepository experimentStatusRepository) {
        this.experimentId = UUID.randomUUID().toString();
        this.seeder = seeder;
        this.experimentConfiguration = experimentConfiguration;
        this.experimentCycle = experimentCycle;
        this.experimentStatusRepository = experimentStatusRepository;
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
        // Create a new experiment status instance for this experiment
        experimentStatus = new ExperimentStatus();
        experimentStatus.setExperimentId(experimentId);
        experimentStatus.setStatus(ExperimentState.RUNNING);
        
        // Save the status to the repository so it can be retrieved
        experimentStatusRepository.save(experimentStatus);
        
        logger.info("Experiment {} running {} cycles", experimentId, experimentConfiguration.getCycleCount());
        
        try {
            // Seed the repository with the experiment ID
            seeder.seed(experimentId);
            
             // Run experiment cycles
             int cycleCount = experimentConfiguration.getCycleCount();
             for (int i = 0; i < cycleCount; i++) {
                 experimentCycle.mutationCycle(experimentId, experimentStatus);
                 experimentStatus.incrementCyclesCompleted();
                 
                 // Log progress every 100 cycles
                 if ((i + 1) % 100 == 0) {
                     logger.info("Cycles run:" + (i + 1));
                 }
             }
            
            logger.info("Experiment {} completed", experimentId);
            experimentStatus.setStatus(ExperimentState.STOPPED);
        } catch (Exception e) {
            logger.error("Experiment {} failed with exception", experimentId, e);
            experimentStatus.setStatus(ExperimentState.EXCEPTION);
            throw e;
        }
    }
}
