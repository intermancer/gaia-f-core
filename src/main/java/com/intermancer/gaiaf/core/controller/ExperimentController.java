package com.intermancer.gaiaf.core.controller;

import com.intermancer.gaiaf.core.experiment.Seeder;
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

    private final Seeder seeder;
    private final OrganismRepository organismRepository;
    
    @Autowired
    public ExperimentController(OrganismRepository organismRepository,
                                Seeder seeder) {
        this.organismRepository = organismRepository;
        this.seeder = seeder;
    }
    
    /**
     * Seeds the OrganismRepository with the Organisms created by the Seeder and returns all of the Organism IDs.
     */
    @GetMapping("/seed")
    public ResponseEntity<List<String>> seed() {
        seeder.seed();
        return ResponseEntity.ok(organismRepository.getAllOrganismIds());
    }
}