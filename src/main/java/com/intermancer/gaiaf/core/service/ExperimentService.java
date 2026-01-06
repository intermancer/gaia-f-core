package com.intermancer.gaiaf.core.service;

import com.intermancer.gaiaf.core.experiment.Experiment;
import com.intermancer.gaiaf.core.experiment.ExperimentConfiguration;
import com.intermancer.gaiaf.core.experiment.ExperimentStatus;
import com.intermancer.gaiaf.core.experiment.repo.ExperimentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Service class for managing experiment operations.
 * Handles the business logic for starting experiments, managing configuration,
 * and tracking experiment status.
 */
@Service
public class ExperimentService {

    private final ApplicationContext applicationContext;
    private final ExperimentRepository experimentRepository;
    private final ExperimentConfiguration experimentConfiguration;
    private final ExperimentStatus experimentStatus;

    @Autowired
    public ExperimentService(ApplicationContext applicationContext,
                             ExperimentRepository experimentRepository,
                             ExperimentConfiguration experimentConfiguration,
                             ExperimentStatus experimentStatus) {
        this.applicationContext = applicationContext;
        this.experimentRepository = experimentRepository;
        this.experimentConfiguration = experimentConfiguration;
        this.experimentStatus = experimentStatus;
    }

    /**
     * Starts a new experiment by instantiating it using ApplicationContext,
     * saving it to the repository, and calling its runExperiment() method.
     *
     * @return a confirmation message
     */
    public String startExperiment() {
        // Instantiate Experiment using ApplicationContext to resolve autowired dependencies
        Experiment experiment = applicationContext.getBean(Experiment.class);

        // Save experiment to repository
        experimentRepository.save(experiment);

        // Start the experiment
        experiment.runExperiment();

        return "Experiment started";
    }

    /**
     * Retrieves the current experiment configuration.
     *
     * @return the current ExperimentConfiguration
     */
    public ExperimentConfiguration getConfiguration() {
        return experimentConfiguration;
    }

    /**
     * Updates the experiment configuration with new values.
     *
     * @param updatedConfig the new configuration values
     * @return the updated configuration
     */
    public ExperimentConfiguration updateConfiguration(ExperimentConfiguration updatedConfig) {
        experimentConfiguration.setCycleCount(updatedConfig.getCycleCount());
        experimentConfiguration.setRepoCapacity(updatedConfig.getRepoCapacity());
        return experimentConfiguration;
    }

    /**
     * Retrieves the current experiment status.
     *
     * @return the current ExperimentStatus
     */
    public ExperimentStatus getStatus() {
        return experimentStatus;
    }
}
