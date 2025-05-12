package com.intermancer.gaiaf.core.organism.breeding;

import java.util.List;

import com.intermancer.gaiaf.core.organism.Organism;

/**
 * Interface for classes that breed two or more Organisms in order to generate
 * descendants that are some sort of combination of the genetic material of their ancestors.
 */
public interface OrganismBreeder {
    
    /**
     * Returns a list of Organisms that have been generated based on some sort
     * of combination of the parental organisms provided as arguments.
     * 
     * @param parents The list of parent Organisms to breed
     * @return A list of descendant Organisms
     */
    List<Organism> breed(List<Organism> parents);
}