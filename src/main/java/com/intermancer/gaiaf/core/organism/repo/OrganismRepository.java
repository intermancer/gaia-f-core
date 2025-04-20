package com.intermancer.gaiaf.core.organism.repo;

import com.intermancer.gaiaf.core.organism.Organism;
import java.util.List;

public interface OrganismRepository {
    Organism getOrganismById(String organismId);
    Organism saveOrganism(Organism organism);
    void deleteOrganism(String organismId);
    List<String> getAllOrganismIds();
}