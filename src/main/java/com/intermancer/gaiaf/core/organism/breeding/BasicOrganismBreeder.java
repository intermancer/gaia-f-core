package com.intermancer.gaiaf.core.organism.breeding;

import org.springframework.stereotype.Component;

import com.intermancer.gaiaf.core.organism.Organism;
import com.intermancer.gaiaf.core.organism.Chromosome;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * BasicBreeder functions like a genetic carousel, creating children by rotating parental chromosomes.
 * 
 * Each child is formed by taking chromosomes from parents in a rotating pattern:
 * - Child 1: takes 1st chromosome from Parent 1, 2nd from Parent 2, etc., cycling back to Parent 1
 * - Child 2: takes 1st chromosome from Parent 2, 2nd from Parent 3, etc.
 * 
 * This pattern continues until creating as many children as there were parents.
 */
@Component
public class BasicOrganismBreeder implements OrganismBreeder {

    @Override
    public List<Organism> breed(List<Organism> parents) {
        if (parents == null || parents.isEmpty()) {
            return new ArrayList<>();
        }

        // We'll create as many children as there are parents
        int numChildren = parents.size();
        List<Organism> children = new ArrayList<>(numChildren);
        
        // Number of chromosomes to collect (use maximum from any parent)
        int maxChromosomes = getMaxChromosomeCount(parents);
            
        // Create each child
        for (int i = 0; i < numChildren; i++) {
            // Generate unique ID for the child
            String childId = UUID.randomUUID().toString().substring(0, 8);
            Organism child = new Organism(childId);
            
            // Collect chromosomes from parents in rotating order
            for (int chromPos = 0; chromPos < maxChromosomes; chromPos++) {
                // Determine which parent to take this chromosome from
                int parentIndex = (i + chromPos) % parents.size();
                Organism parent = parents.get(parentIndex);
                
                // Only add chromosome if parent has enough chromosomes
                if (chromPos < parent.getChromosomes().size()) {
                    // Add a copy of the chromosome to the child
                    Chromosome chromosome = parent.getChromosomes().get(chromPos).copyOf();
                    child.addChromosome(chromosome);
                }
            }
            
            children.add(child);
        }
        
        return children;
    }
    
    /**
     * Finds the maximum number of chromosomes in any parent organism.
     * 
     * @param parents The list of parent organisms
     * @return The maximum number of chromosomes
     */
    private int getMaxChromosomeCount(List<Organism> parents) {
        int maxChromosomes = 0;
        for (Organism parent : parents) {
            maxChromosomes = Math.max(maxChromosomes, parent.getChromosomes().size());
        }
        return maxChromosomes;
    }
}