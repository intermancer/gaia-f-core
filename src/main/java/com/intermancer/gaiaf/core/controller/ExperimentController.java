package com.intermancer.gaiaf.core.controller;

import com.intermancer.gaiaf.core.evaluate.ScoredOrganismRepository;
import com.intermancer.gaiaf.core.experiment.Seeder;
import com.intermancer.gaiaf.core.experiment.Experiment;
import com.intermancer.gaiaf.core.experiment.ExperimentConfiguration;
import com.intermancer.gaiaf.core.experiment.ExperimentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/experiment")
public class ExperimentController {

    private final Seeder seeder;
    private final ScoredOrganismRepository scoredOrganismRepository;
    private final Experiment experiment;
    private final ExperimentConfiguration experimentConfiguration;
    private final ExperimentStatus experimentStatus;

    @Autowired
    public ExperimentController(ScoredOrganismRepository scoredOrganismRepository,
                                Seeder seeder,
                                Experiment experiment,
                                ExperimentConfiguration experimentConfiguration,
                                ExperimentStatus experimentStatus) {
        this.scoredOrganismRepository = scoredOrganismRepository;
        this.seeder = seeder;
        this.experiment = experiment;
        this.experimentConfiguration = experimentConfiguration;
        this.experimentStatus = experimentStatus;
    }

    /**
     * Seeds the OrganismRepository with the Organisms created by the Seeder and returns all of the Organism IDs.
     */
    @GetMapping("/seed")
    public ResponseEntity<List<String>> seed() {
        seeder.seed();
        return ResponseEntity.ok(scoredOrganismRepository.getAllOrganismIds());
    }

    /**
     * Starts the experiment by calling the Experiment.runExperiment() method.
     * Returns a confirmation message.
     */
    @PostMapping("/start")
    public ResponseEntity<String> startExperiment() {
        experiment.runExperiment();
        return ResponseEntity.ok("Experiment started");
    }

    /**
     * Returns the current ExperimentConfiguration.
     */
    @GetMapping("/configuration")
    public ResponseEntity<ExperimentConfiguration> getConfiguration() {
        return ResponseEntity.ok(experimentConfiguration);
    }

    /**
     * Updates the experiment configuration with new values for cycleCount and repoCapacity.
     * Returns the updated configuration.
     * 
     * @param updatedConfig The new configuration values
     * @return The updated configuration
     */
    @PutMapping("/configuration")
    public ResponseEntity<ExperimentConfiguration> updateConfiguration(@RequestBody ExperimentConfiguration updatedConfig) {
        experimentConfiguration.setCycleCount(updatedConfig.getCycleCount());
        experimentConfiguration.setRepoCapacity(updatedConfig.getRepoCapacity());
        return ResponseEntity.ok(experimentConfiguration);
    }

    /**
     * Returns the current ExperimentStatus containing cyclesCompleted, organismsReplaced,
     * and the current experiment state.
     */
    @GetMapping("/status")
    public ResponseEntity<ExperimentStatus> getStatus() {
        return ResponseEntity.ok(experimentStatus);
    }
}