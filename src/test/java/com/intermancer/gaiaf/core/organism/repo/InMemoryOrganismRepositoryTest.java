package com.intermancer.gaiaf.core.organism.repo;

import com.intermancer.gaiaf.core.organism.Organism;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryOrganismRepositoryTest {

    @Test
    void testSaveAndRetrieveOrganism() {
        OrganismRepository repository = new InMemoryOrganismRepository();
        Organism organism = new Organism("1");
        repository.saveOrganism(organism);

        Organism retrieved = repository.getOrganismById("1");
        assertEquals(organism, retrieved);
    }

    @Test
    void testDeleteOrganism() {
        OrganismRepository repository = new InMemoryOrganismRepository();
        Organism organism = new Organism("1");
        repository.saveOrganism(organism);

        repository.deleteOrganism("1");
        assertThrows(OrganismNotFoundException.class, () -> repository.getOrganismById("1"));
    }

    @Test
    void testGetAllOrganismIds() {
        OrganismRepository repository = new InMemoryOrganismRepository();
        Organism organism1 = new Organism("1");
        Organism organism2 = new Organism("2");
        repository.saveOrganism(organism1);
        repository.saveOrganism(organism2);

        List<String> organismIds = repository.getAllOrganismIds();
        assertEquals(2, organismIds.size());
        assertTrue(organismIds.contains("1"));
        assertTrue(organismIds.contains("2"));
    }

    @Test
    void testOrganismNotFoundException() {
        OrganismRepository repository = new InMemoryOrganismRepository();
        assertThrows(OrganismNotFoundException.class, () -> repository.getOrganismById("nonexistent"));
    }
}