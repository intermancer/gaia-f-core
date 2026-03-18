package com.intermancer.gaiaf.core.controller;

import com.intermancer.gaiaf.core.dto.ExperimentSummary;
import com.intermancer.gaiaf.core.dto.PaginatedResponse;
import com.intermancer.gaiaf.core.dto.ScoredOrganismSummary;
import com.intermancer.gaiaf.core.evaluate.ScoredOrganism;
import com.intermancer.gaiaf.core.experiment.ExperimentConfiguration;
import com.intermancer.gaiaf.core.experiment.ExperimentStatus;
import com.intermancer.gaiaf.core.service.ExperimentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for experiment endpoints.
 * Delegates business logic to ExperimentService.
 */
@RestController
@RequestMapping("/experiment")
public class ExperimentController {

    private static final Logger logger = LoggerFactory.getLogger(ExperimentController.class);
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
        logger.info("Pinging status...");
        return ResponseEntity.ok(experimentService.getStatus(experimentId));
    }
    
    /**
     * Pauses a running experiment.
     *
     * @param experimentId the ID of the experiment to pause
     * @return HTTP 200 OK on success, HTTP 400 Bad Request if not in pausable state
     */
    @PostMapping("/{experimentId}/pause")
    public ResponseEntity<Void> pauseExperiment(@PathVariable String experimentId) {
        try {
            experimentService.pauseExperiment(experimentId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Cannot pause experiment {}: {}", experimentId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Resumes a paused experiment.
     *
     * @param experimentId the ID of the experiment to resume
     * @return HTTP 200 OK on success, HTTP 400 Bad Request if not in PAUSED state
     */
    @PostMapping("/{experimentId}/resume")
    public ResponseEntity<Void> resumeExperiment(@PathVariable String experimentId) {
        try {
            experimentService.resumeExperiment(experimentId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Cannot resume experiment {}: {}", experimentId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Returns a list of all experiments with summary information.
     *
     * @return List of ExperimentSummary objects
     */
    @GetMapping("/list")
    public ResponseEntity<List<ExperimentSummary>> getAllExperiments() {
        return ResponseEntity.ok(experimentService.getAllExperiments());
    }

    /**
     * Returns a paginated list of scored organisms for an experiment.
     *
     * @param experimentId The experiment ID
     * @param offset Starting index (default 0)
     * @param limit Maximum results (default 50)
     * @return PaginatedResponse containing scored organism summaries
     */
    @GetMapping("/{experimentId}/scored-organisms")
    public ResponseEntity<PaginatedResponse<ScoredOrganismSummary>> getScoredOrganisms(
            @PathVariable String experimentId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(
            experimentService.getScoredOrganismsForExperiment(experimentId, offset, limit));
    }

    /**
     * Returns the full detail of a scored organism.
     *
     * @param id The scored organism ID
     * @return ScoredOrganism with full organism JSON
     */
    @GetMapping("/scored-organism/{id}")
    public ResponseEntity<ScoredOrganism> getScoredOrganismDetail(@PathVariable String id) {
        try {
            return ResponseEntity.ok(experimentService.getScoredOrganismById(id));
        } catch (IllegalArgumentException e) {
            logger.error("Scored organism not found: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}