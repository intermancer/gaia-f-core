package com.intermancer.gaiaf.core.controller;

import com.intermancer.gaiaf.core.experiment.ExperimentConfiguration;
import com.intermancer.gaiaf.core.experiment.ExperimentStatus;
import com.intermancer.gaiaf.core.service.ExperimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for experiment endpoints.
 * Delegates business logic to ExperimentService.
 */
@RestController
@RequestMapping("/experiment")
public class ExperimentController {

    private final ExperimentService experimentService;

    @Autowired
    public ExperimentController(ExperimentService experimentService) {
        this.experimentService = experimentService;
    }

    /**
     * Starts a new experiment.
     *
     * @return the ID of the started experiment
     */
    @PostMapping("/start")
    public ResponseEntity<String> startExperiment() {
        String result = experimentService.startExperiment();
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves the current configuration of the configuration component.
     * This is the configuration that will be used for the next experiment.
     *
     * @return the current ExperimentConfiguration
     */
    @GetMapping("/configuration")
    public ResponseEntity<ExperimentConfiguration> getComponentConfiguration() {
        return ResponseEntity.ok(experimentService.getComponentConfiguration());
    }

    /**
     * Updates the configuration of the configuration component.
     * This affects the configuration that will be used for the next experiment.
     *
     * @param updatedConfig the new configuration values
     * @return the updated configuration
     */
    @PutMapping("/configuration")
    public ResponseEntity<ExperimentConfiguration> updateComponentConfiguration(@RequestBody ExperimentConfiguration updatedConfig) {
        ExperimentConfiguration result = experimentService.updateComponentConfiguration(updatedConfig);
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves the configuration that was loaded when a specific experiment was created.
     *
     * @param experimentId the ID of the experiment
     * @return the ExperimentConfiguration for the given experiment
     */
    @GetMapping("/{experimentId}/configuration")
    public ResponseEntity<ExperimentConfiguration> getExperimentConfiguration(@PathVariable String experimentId) {
        return ResponseEntity.ok(experimentService.getExperimentConfiguration(experimentId));
    }

    /**
     * Retrieves the status for a specific experiment.
     *
     * @param experimentId the ID of the experiment
     * @return the ExperimentStatus
     */
    @GetMapping("/{experimentId}/status")
    public ResponseEntity<ExperimentStatus> getStatus(@PathVariable String experimentId) {
        return ResponseEntity.ok(experimentService.getStatus(experimentId));
    }
}