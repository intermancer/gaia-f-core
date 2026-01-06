package com.intermancer.gaiaf.core.experiment;

import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * A data class that tracks the runtime state and progress of an experiment.
 * ExperimentStatus maintains information about the experiment's current execution state,
 * performance metrics, and operational statistics.
 * 
 * ExperimentStatus instances are created and managed by the ExperimentController.
 * When an experiment starts, the controller creates a new ExperimentStatus instance
 * and persists it to the ExperimentStatusRepository.
 */
@Component
public class ExperimentStatus {

    private int cyclesCompleted = 0;
    private int organismsReplaced = 0;
    private ExperimentState status = ExperimentState.STOPPED;
    private String experimentId;
    private String id;

    /**
     * Default constructor that generates a unique ID for this ExperimentStatus instance.
     */
    public ExperimentStatus() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Gets the number of experiment cycles that have been completed.
     *
     * @return the number of completed cycles
     */
    public int getCyclesCompleted() {
        return cyclesCompleted;
    }

    /**
     * Sets the number of experiment cycles that have been completed.
     *
     * @param cyclesCompleted the number of completed cycles
     */
    public void setCyclesCompleted(int cyclesCompleted) {
        this.cyclesCompleted = cyclesCompleted;
    }

    /**
     * Gets the count of organisms that have been replaced in the ScoredOrganismRepository.
     *
     * @return the number of organisms replaced
     */
    public int getOrganismsReplaced() {
        return organismsReplaced;
    }

    /**
     * Sets the count of organisms that have been replaced.
     *
     * @param organismsReplaced the number of organisms replaced
     */
    public void setOrganismsReplaced(int organismsReplaced) {
        this.organismsReplaced = organismsReplaced;
    }

    /**
     * Gets the current operational state of the experiment.
     *
     * @return the current experiment state
     */
    public ExperimentState getStatus() {
        return status;
    }

    /**
     * Sets the current operational state of the experiment.
     *
     * @param status the experiment state to set
     */
    public void setStatus(ExperimentState status) {
        this.status = status;
    }
    
    public String getExperimentId() {
        return experimentId;
    }
    
    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Resets all tracking metrics to their initial state.
     * Sets cyclesCompleted to 0, organismsReplaced to 0, and status to STOPPED.
     */
    public void reset() {
        this.cyclesCompleted = 0;
        this.organismsReplaced = 0;
        this.status = ExperimentState.STOPPED;
    }

    /**
     * Increments the cyclesCompleted counter by 1.
     * Called after each successful experiment cycle.
     */
    public void incrementCyclesCompleted() {
        this.cyclesCompleted++;
    }

    /**
     * Increments the organismsReplaced counter by 1.
     * Called when an organism is successfully replaced in the ScoredOrganismRepository.
     */
    public void incrementOrganismsReplaced() {
        this.organismsReplaced++;
    }

    /**
     * Increments the organismsReplaced counter by the specified count.
     * Used when multiple organisms are replaced in a single maintenance operation.
     *
     * @param count the number of organisms replaced
     */
    public void incrementOrganismsReplaced(int count) {
        this.organismsReplaced += count;
    }
}
