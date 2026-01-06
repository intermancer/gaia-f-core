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
}
