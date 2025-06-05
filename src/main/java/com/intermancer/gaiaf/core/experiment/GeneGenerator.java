package com.intermancer.gaiaf.core.experiment;

import java.util.Random;
import java.util.UUID;

import com.intermancer.gaiaf.core.organism.Gene;
import com.intermancer.gaiaf.core.organism.gene.basic.AdditionGene;
import com.intermancer.gaiaf.core.organism.gene.basic.DivisionGene;
import com.intermancer.gaiaf.core.organism.gene.basic.MultiplicationGene;
import com.intermancer.gaiaf.core.organism.gene.basic.SineGene;
import com.intermancer.gaiaf.core.organism.gene.basic.SubtractionGene;

/**
 * Utility class for generating random genes to support mutation activities.
 */
public class GeneGenerator {
    
    private static final Random random = new Random();
    
    /**
     * Returns a single, randomly chosen Gene. If the Gene uses any operational 
     * constants, they are randomly generated. If the Gene has one targetIndex, 
     * it uses the default value of -1. If the Gene has more than one targetIndex, 
     * the others will be given random numbers between -2 and -10, with none of 
     * them repeating.
     * 
     * @return A randomly generated Gene
     */
    public static Gene getRandomGene() {
        // Choose a random gene type
        int geneType = random.nextInt(5);
        Gene gene;
        
        switch (geneType) {
            case 0:
                gene = new AdditionGene();
                break;
            case 1:
                gene = new SubtractionGene();
                break;
            case 2:
                gene = new MultiplicationGene();
                break;
            case 3:
                gene = new DivisionGene();
                break;
            case 4:
                gene = new SineGene();
                break;
            default:
                gene = new AdditionGene();
        }
        
        // Set a random ID
        gene.setId(gene.getClass().getSimpleName() + "-" + UUID.randomUUID().toString().substring(0, 8));
        
        // For genes with operation constants, randomize them
        if (!gene.getOperationConstantList().isEmpty()) {
            gene.getOperationConstantList().clear();
            // Generate a random constant between 0.1 and 10.0
            double randomConstant = 0.1 + (random.nextDouble() * 9.9);
            gene.getOperationConstantList().add(randomConstant);
        }
        
        // Currently all basic genes have only one target index, so we keep the default -1
        // If we had genes with multiple target indices, we would implement the logic here:
        /*
        if (gene.getTargetIndexList().size() > 1) {
            List<Integer> usedIndices = new ArrayList<>();
            usedIndices.add(-1); // First index remains -1
            
            for (int i = 1; i < gene.getTargetIndexList().size(); i++) {
                int newIndex;
                do {
                    newIndex = -(random.nextInt(9) + 2); // Random between -2 and -10
                } while (usedIndices.contains(newIndex));  
                usedIndices.add(newIndex);
                gene.getTargetIndexList().set(i, newIndex);
            }
        }
        */
        
        return gene;
    }
}