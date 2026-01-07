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
     * Retrieves the current configuration of the configuration component.
     * This is the configuration that will be used for the next experiment.
     *
     * @return the current ExperimentConfiguration
     */
    public ExperimentConfiguration getComponentConfiguration() {
        return experimentConfiguration;
    }

    /**
     * Updates the configuration of the configuration component.
     * This affects the configuration that will be used for the next experiment.
     *
     * @param updatedConfig the new configuration values
     * @return the updated configuration
     */
    public ExperimentConfiguration updateComponentConfiguration(ExperimentConfiguration updatedConfig) {
        experimentConfiguration.setCycleCount(updatedConfig.getCycleCount());
        experimentConfiguration.setRepoCapacity(updatedConfig.getRepoCapacity());
        return experimentConfiguration;
    }

    /**
     * Retrieves the configuration that was loaded when a specific experiment was created.
     *
     * @param experimentId the ID of the experiment
     * @return the ExperimentConfiguration for the given experiment
     */
    public ExperimentConfiguration getExperimentConfiguration(String experimentId) {
        // Look up the experiment by ID to get its configuration
        Experiment experiment = experimentRepository.findById(experimentId)
            .orElseThrow(() -> new IllegalArgumentException("No experiment found with ID: " + experimentId));
        
        // Note: Currently, the configuration is a singleton component
        // In the future, this could be enhanced to store experiment-specific configurations
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
