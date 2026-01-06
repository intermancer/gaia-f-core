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
     * @return a confirmation message
     */
    @PostMapping("/start")
    public ResponseEntity<String> startExperiment() {
        String result = experimentService.startExperiment();
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves the current experiment configuration.
     *
     * @return the current ExperimentConfiguration
     */
    @GetMapping("/configuration")
    public ResponseEntity<ExperimentConfiguration> getConfiguration() {
        return ResponseEntity.ok(experimentService.getConfiguration());
    }

    /**
     * Updates the experiment configuration with new values.
     *
     * @param updatedConfig the new configuration values
     * @return the updated configuration
     */
    @PutMapping("/configuration")
    public ResponseEntity<ExperimentConfiguration> updateConfiguration(@RequestBody ExperimentConfiguration updatedConfig) {
        ExperimentConfiguration result = experimentService.updateConfiguration(updatedConfig);
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves the current experiment status.
     *
     * @return the current ExperimentStatus
     */
    @GetMapping("/status")
    public ResponseEntity<ExperimentStatus> getStatus() {
        return ResponseEntity.ok(experimentService.getStatus());
    }
}