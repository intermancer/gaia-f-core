package com.intermancer.gaiaf.core.controller;

import com.intermancer.gaiaf.core.experiment.Experiment;
import com.intermancer.gaiaf.core.organism.repo.OrganismRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/experiment")
public class ExperimentController {
    
    private final Experiment experiment;
    private final OrganismRepository organismRepository;
    
    @Autowired
    public ExperimentController(Experiment experiment, OrganismRepository organismRepository) {
        this.experiment = experiment;
        this.organismRepository = organismRepository;
    }
    
    /**
     * Seeds the OrganismRepository with the Organisms created by the Seeder and returns all of the Organism IDs.
     */
    @GetMapping("/seed")
    public ResponseEntity<List<String>> seed() {
        experiment.seed();
        return ResponseEntity.ok(organismRepository.getAllOrganismIds());
    }
}