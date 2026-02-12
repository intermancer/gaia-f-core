package com.intermancer.gaiaf.core.experiment;

import com.intermancer.gaiaf.core.experiment.repo.ExperimentStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
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
    private final Instant createdAt;
    private final Seeder seeder;
    private final ExperimentConfiguration experimentConfiguration;
    private final ExperimentCycle experimentCycle;
    private final ExperimentStatusRepository experimentStatusRepository;
    private ExperimentStatus experimentStatus;
    private volatile boolean paused = false;
    private boolean pausable;
    private int pauseCycles;
    
    @Autowired
    public BasicExperimentImpl(Seeder seeder,
                               ExperimentConfiguration experimentConfiguration,
                               ExperimentCycle experimentCycle,
                               ExperimentStatusRepository experimentStatusRepository) {
        this.experimentId = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
        this.seeder = seeder;
        this.experimentConfiguration = experimentConfiguration;
        this.experimentCycle = experimentCycle;
        this.experimentStatusRepository = experimentStatusRepository;
    }
    
    @Override
    public String getId() {
        return experimentId;
    }
    
    @Override
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Executes the complete experiment process:
     * 1. Seeds the ScoredOrganismRepository by calling the Seeder 
     *    (the Seeder evaluates organisms and stores them)
     * 2. Runs the number of experiment cycles specified in ExperimentConfiguration
     */
    @Override
    public void runExperiment() {
        // Copy pausable and pauseCycles from configuration
        this.pausable = experimentConfiguration.isPausable();
        this.pauseCycles = experimentConfiguration.getPauseCycles();
        
        // Create a new experiment status instance for this experiment
        experimentStatus = new ExperimentStatus();
        experimentStatus.setExperimentId(experimentId);
        experimentStatus.setStatus(ExperimentState.RUNNING);
        
        // Save the status to the repository so it can be retrieved
        experimentStatusRepository.save(experimentStatus);
        
        logger.info("Experiment {} running {} cycles (pausable: {}, pauseCycles: {})", 
            experimentId, experimentConfiguration.getCycleCount(), pausable, pauseCycles);
        
        try {
            // Seed the repository with the experiment ID
            seeder.seed(experimentId);
            
             // Run experiment cycles
             int cycleCount = experimentConfiguration.getCycleCount();
             for (int i = 0; i < cycleCount; i++) {
                 // Check if paused and wait if necessary
                 synchronized (this) {
                     while (paused) {
                         logger.debug("Experiment {} paused, waiting...", experimentId);
                         try {
                             wait();
                         } catch (InterruptedException e) {
                             Thread.currentThread().interrupt();
                             logger.warn("Experiment {} interrupted while paused", experimentId);
                             throw new RuntimeException("Experiment interrupted while paused", e);
                         }
                     }
                 }
                 
                 experimentCycle.mutationCycle(experimentId, experimentStatus);
                 experimentStatus.incrementCyclesCompleted();
                 
                 // Check for auto-pause at regular intervals (every pauseCycles cycles)
                 if (pausable && pauseCycles > 0 && experimentStatus.getCyclesCompleted() % pauseCycles == 0) {
                     logger.info("Experiment {} auto-pausing at cycle {}", experimentId, experimentStatus.getCyclesCompleted());
                     pause();
                 }
                 
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
    
    @Override
    public void pause() {
        if (experimentStatus != null && experimentStatus.getStatus() == ExperimentState.RUNNING) {
            logger.info("Pausing experiment {}", experimentId);
            paused = true;
            experimentStatus.setStatus(ExperimentState.PAUSED);
        } else {
            logger.warn("Cannot pause experiment {} - current state: {}", 
                experimentId, experimentStatus != null ? experimentStatus.getStatus() : "null");
        }
    }
    
    @Override
    public synchronized void resume() {
        if (experimentStatus != null && experimentStatus.getStatus() == ExperimentState.PAUSED) {
            logger.info("Resuming experiment {}", experimentId);
            paused = false;
            experimentStatus.setStatus(ExperimentState.RUNNING);
            notifyAll();
        } else {
            logger.warn("Cannot resume experiment {} - current state: {}", 
                experimentId, experimentStatus != null ? experimentStatus.getStatus() : "null");
        }
    }
    
    @Override
    public boolean isPaused() {
        return paused;
    }
}
