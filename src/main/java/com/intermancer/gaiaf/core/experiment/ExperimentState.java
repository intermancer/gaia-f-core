package com.intermancer.gaiaf.core.experiment;

/**
 * The possible operational states of an experiment.
 */
public enum ExperimentState {
    /** The experiment is not currently running */
    STOPPED,
    /** The experiment is actively executing cycles */
    RUNNING,
    /** The experiment encountered an error and has stopped */
    EXCEPTION
}
