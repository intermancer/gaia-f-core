package com.intermancer.gaiaf.core.service;

import com.intermancer.gaiaf.core.experiment.Experiment;
import com.intermancer.gaiaf.core.experiment.ExperimentConfiguration;
import com.intermancer.gaiaf.core.experiment.ExperimentStatus;
import com.intermancer.gaiaf.core.experiment.repo.ExperimentRepository;
import com.intermancer.gaiaf.core.experiment.repo.ExperimentStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Service class for managing experiment operations.
 * Handles the business logic for starting experiments, managing configuration,
 * and tracking experiment status for specific experiments.
 */
@Service
public class ExperimentService {

    private final ApplicationContext applicationContext;
    private final ExperimentRepository experimentRepository;
    private final ExperimentStatusRepository experimentStatusRepository;
    private final ExperimentConfiguration experimentConfiguration;

    @Autowired
    public ExperimentService(ApplicationContext applicationContext,
                             ExperimentRepository experimentRepository,
                             ExperimentStatusRepository experimentStatusRepository,
                             ExperimentConfiguration experimentConfiguration) {
        this.applicationContext = applicationContext;
        this.experimentRepository = experimentRepository;
        this.experimentStatusRepository = experimentStatusRepository;
        this.experimentConfiguration = experimentConfiguration;
    }

    /**
     * Starts a new experiment by instantiating it using ApplicationContext,
     * saving it to the repository, and calling its runExperiment() method.
     *
     * @return the ID of the started experiment
     */
    public String startExperiment() {
        // Instantiate Experiment using ApplicationContext to resolve autowired dependencies
        Experiment experiment = applicationContext.getBean(Experiment.class);

        // Save experiment to repository
        experimentRepository.save(experiment);

        // Start the experiment
        experiment.runExperiment();

        return experiment.getId();
    }

    /**
     * Retrieves the configuration for a specific experiment.
     *
     * @param experimentId the ID of the experiment
     * @return the ExperimentConfiguration
     */
    public ExperimentConfiguration getConfiguration(String experimentId) {
        // Configuration is currently global; retrieve the shared configuration
        return experimentConfiguration;
    }

    /**
     * Updates the configuration for a specific experiment with new values.
     *
     * @param experimentId the ID of the experiment
     * @param updatedConfig the new configuration values
     * @return the updated configuration
     */
    public ExperimentConfiguration updateConfiguration(String experimentId, ExperimentConfiguration updatedConfig) {
        // Configuration is currently global; update the shared configuration
        experimentConfiguration.setCycleCount(updatedConfig.getCycleCount());
        experimentConfiguration.setRepoCapacity(updatedConfig.getRepoCapacity());
        return experimentConfiguration;
    }

    /**
     * Retrieves the status for a specific experiment.
     *
     * @param experimentId the ID of the experiment
     * @return the ExperimentStatus for the given experiment ID
     */
    public ExperimentStatus getStatus(String experimentId) {
        // Try to find the status for the specific experiment
        var statusOptional = experimentStatusRepository.findByExperimentId(experimentId);
        if (statusOptional.isPresent()) {
            return statusOptional.get();
        }
        // Return empty/default status if not found
        throw new IllegalArgumentException("No status found for experiment ID: " + experimentId);
    }
}
