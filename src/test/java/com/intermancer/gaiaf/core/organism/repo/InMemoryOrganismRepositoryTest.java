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
    void testGetAllOrganisms() {
        OrganismRepository repository = new InMemoryOrganismRepository();
        Organism organism1 = new Organism("1");
        Organism organism2 = new Organism("2");
        repository.saveOrganism(organism1);
        repository.saveOrganism(organism2);

        List<Organism> organisms = repository.getAllOrganisms();
        assertEquals(2, organisms.size());
        assertTrue(organisms.contains(organism1));
        assertTrue(organisms.contains(organism2));
    }

    @Test
    void testOrganismNotFoundException() {
        OrganismRepository repository = new InMemoryOrganismRepository();
        assertThrows(OrganismNotFoundException.class, () -> repository.getOrganismById("nonexistent"));
    }
}