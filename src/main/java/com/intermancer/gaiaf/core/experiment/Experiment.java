package com.intermancer.gaiaf.core.experiment;

/**
 * An Experiment is responsible for orchestrating the complete experimentation process.
 * It manages seeding the ScoredOrganismRepository with initial evaluated organisms
 * and executing multiple Experiment Cycles.
 */
public interface Experiment {
    
    /**
     * Executes the complete experiment process:
     * 1. Seeds the ScoredOrganismRepository by calling the Seeder 
     *    (the Seeder evaluates organisms and stores them)
     * 2. Runs the number of experiment cycles specified in ExperimentConfiguration
     */
    void runExperiment();
    
    /**
     * Returns the unique identifier for this experiment.
     * 
     * @return The experiment ID
     */
    String getId();
    
    /**
     * Pauses the experiment execution. The experiment will stop processing cycles
     * but maintain its current state for later resumption.
     * Only valid when the experiment is in RUNNING state.
     */
    void pause();
    
    /**
     * Resumes a paused experiment. The experiment will continue processing cycles
     * from where it left off. Only valid when the experiment is in PAUSED state.
     */
    void resume();
    
    /**
     * Returns true if the experiment is currently paused, false otherwise.
     * 
     * @return true if paused, false otherwise
     */
    boolean isPaused();
}
