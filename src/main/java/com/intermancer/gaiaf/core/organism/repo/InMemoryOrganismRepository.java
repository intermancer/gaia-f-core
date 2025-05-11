package com.intermancer.gaiaf.core.organism.repo;

import com.intermancer.gaiaf.core.organism.Organism;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the OrganismRepository interface.
 * Stores organisms in a simple HashMap.
 */
@Component
public class InMemoryOrganismRepository implements OrganismRepository {
    private final Map<String, Organism> organismMap = new HashMap<>();

    /**
     * Retrieves an organism by its ID.
     * 
     * @param organismId The ID of the organism to retrieve.
     * @return The organism with the specified ID.
     * @throws OrganismNotFoundException if no organism with the given ID exists.
     */
    @Override
    public Organism getOrganismById(String organismId) {
        if (!organismMap.containsKey(organismId)) {
            throw new OrganismNotFoundException("Organism with ID " + organismId + " not found.");
        }
        return organismMap.get(organismId);
    }

    /**
     * Saves or updates an organism in the repository.
     * 
     * @param organism The organism to save.
     * @return The saved organism.
     */
    @Override
    public Organism saveOrganism(Organism organism) {
        organismMap.put(organism.getId(), organism);
        return organism;
    }

    /**
     * Deletes an organism from the repository.
     * 
     * @param organismId The ID of the organism to delete.
     * @throws OrganismNotFoundException if no organism with the given ID exists.
     */
    @Override
    public void deleteOrganism(String organismId) {
        if (!organismMap.containsKey(organismId)) {
            throw new OrganismNotFoundException("Organism with ID " + organismId + " not found.");
        }
        organismMap.remove(organismId);
    }

    /**
     * Retrieves all IDs of organisms stored in the repository.
     * 
     * @return A list of all organism IDs.
     */
    @Override
    public List<String> getAllOrganismIds() {
        return organismMap.keySet().stream().collect(Collectors.toList());
    }
}