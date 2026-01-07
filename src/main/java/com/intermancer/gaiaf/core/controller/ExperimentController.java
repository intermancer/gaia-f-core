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
     * Retrieves the configuration for a specific experiment.
     *
     * @param experimentId the ID of the experiment
     * @return the ExperimentConfiguration
     */
    @GetMapping("/{experimentId}/configuration")
    public ResponseEntity<ExperimentConfiguration> getConfiguration(@PathVariable String experimentId) {
        return ResponseEntity.ok(experimentService.getConfiguration(experimentId));
    }

    /**
     * Updates the configuration for a specific experiment with new values.
     *
     * @param experimentId the ID of the experiment
     * @param updatedConfig the new configuration values
     * @return the updated configuration
     */
    @PutMapping("/{experimentId}/configuration")
    public ResponseEntity<ExperimentConfiguration> updateConfiguration(@PathVariable String experimentId, @RequestBody ExperimentConfiguration updatedConfig) {
        ExperimentConfiguration result = experimentService.updateConfiguration(experimentId, updatedConfig);
        return ResponseEntity.ok(result);
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