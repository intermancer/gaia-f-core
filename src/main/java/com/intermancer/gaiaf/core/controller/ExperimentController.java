package com.intermancer.gaiaf.core.controller;

import com.intermancer.gaiaf.core.evaluate.ScoredOrganismRepository;
import com.intermancer.gaiaf.core.experiment.Experiment;
import com.intermancer.gaiaf.core.experiment.ExperimentConfiguration;
import com.intermancer.gaiaf.core.experiment.ExperimentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/experiment")
public class ExperimentController {

    private final ApplicationContext applicationContext;
    private final ScoredOrganismRepository scoredOrganismRepository;
    private final ExperimentConfiguration experimentConfiguration;
    private final ExperimentStatus experimentStatus;
    
    private final List<Experiment> experiments;

    @Autowired
    public ExperimentController(ApplicationContext applicationContext,
                                ScoredOrganismRepository scoredOrganismRepository,
                                ExperimentConfiguration experimentConfiguration,
                                ExperimentStatus experimentStatus) {
        this.applicationContext = applicationContext;
        this.scoredOrganismRepository = scoredOrganismRepository;
        this.experimentConfiguration = experimentConfiguration;
        this.experimentStatus = experimentStatus;
        this.experiments = new ArrayList<>();
    }

    /**
     * Starts the experiment by instantiating a new Experiment using ApplicationContext,
     * adding it to the experiments list, and calling its runExperiment() method.
     * Returns a confirmation message.
     */
    @PostMapping("/start")
    public ResponseEntity<String> startExperiment() {
        // Instantiate Experiment using ApplicationContext to resolve autowired dependencies
        Experiment experiment = applicationContext.getBean(Experiment.class);
        
        // Add to experiments list
        experiments.add(experiment);
        
        // Start the experiment
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