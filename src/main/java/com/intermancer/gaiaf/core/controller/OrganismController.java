package com.intermancer.gaiaf.core.controller;

import com.intermancer.gaiaf.core.organism.repo.OrganismRepository;
import com.intermancer.gaiaf.core.organism.repo.OrganismNotFoundException;
import com.intermancer.gaiaf.core.organism.Organism;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/organism")
public class OrganismController {

    private final OrganismRepository organismRepository;

    public OrganismController(OrganismRepository organismRepository) {
        this.organismRepository = organismRepository;
    }

    @GetMapping
    public ResponseEntity<List<Organism>> getAllOrganisms() {
        return ResponseEntity.ok(organismRepository.getAllOrganisms());
    }

    @GetMapping("/repo/{organismId}")
    public ResponseEntity<Organism> getOrganismById(@PathVariable String organismId) {
        try {
            Organism organism = organismRepository.getOrganismById(organismId);
            return ResponseEntity.ok(organism);
        } catch (OrganismNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/repo")
    public ResponseEntity<Void> saveOrganism(@RequestBody Organism organism) {
        organismRepository.saveOrganism(organism);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/repo/{organismId}")
    public ResponseEntity<Void> deleteOrganism(@PathVariable String organismId) {
        try {
            organismRepository.deleteOrganism(organismId);
            return ResponseEntity.noContent().build();
        } catch (OrganismNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}