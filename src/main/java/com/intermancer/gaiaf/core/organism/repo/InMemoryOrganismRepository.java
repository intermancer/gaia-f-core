package com.intermancer.gaiaf.core.organism.repo;

import com.intermancer.gaiaf.core.organism.Organism;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryOrganismRepository implements OrganismRepository {
    private final Map<String, Organism> organismMap = new HashMap<>();

    @Override
    public Organism getOrganismById(String organismId) {
        if (!organismMap.containsKey(organismId)) {
            throw new OrganismNotFoundException("Organism with ID " + organismId + " not found.");
        }
        return organismMap.get(organismId);
    }

    @Override
    public Organism saveOrganism(Organism organism) {
        organismMap.put(organism.getId(), organism);
        return organism;
    }

    @Override
    public void deleteOrganism(String organismId) {
        if (!organismMap.containsKey(organismId)) {
            throw new OrganismNotFoundException("Organism with ID " + organismId + " not found.");
        }
        organismMap.remove(organismId);
    }

    @Override
    public List<String> getAllOrganismIds() {
        return organismMap.keySet().stream().collect(Collectors.toList());
    }
}