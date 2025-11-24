package com.intermancer.gaiaf.core.controller;

import com.intermancer.gaiaf.core.evaluate.ScoredOrganismRepository;
import com.intermancer.gaiaf.core.experiment.Seeder;
import com.intermancer.gaiaf.core.experiment.Experiment;
import com.intermancer.gaiaf.core.experiment.ExperimentConfiguration;
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

    @Autowired
    public ExperimentController(ScoredOrganismRepository scoredOrganismRepository,
                                Seeder seeder,
                                Experiment experiment,
                                ExperimentConfiguration experimentConfiguration) {
        this.scoredOrganismRepository = scoredOrganismRepository;
        this.seeder = seeder;
        this.experiment = experiment;
        this.experimentConfiguration = experimentConfiguration;
    }

    /**
     * Seeds the OrganismRepository with the Organisms created by the Seeder and returns all of the Organism IDs.
     */
    @GetMapping("/seed")
    public ResponseEntity<List<String>> seed() {
        seeder.seed();
        return ResponseEntity.ok(scoredOrganismRepository.getAllOrganismIds());
    }

    @PostMapping("/start")
    public ResponseEntity<String> startExperiment() {
        experiment.runExperiment();
        return ResponseEntity.ok("Experiment started");
    }

    @GetMapping("/configuration")
    public ResponseEntity<ExperimentConfiguration> getConfiguration() {
        return ResponseEntity.ok(experimentConfiguration);
    }

    /**
     * Updates the experiment configuration with new values.
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
}