package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.Gene;

/**
 * A Gene that subtracts a constant value from a DataPoint.
 */
public class SubtractionGene extends Gene {
    
    public SubtractionGene() {
        super();
        // Initialize operationConstantList with 1.5 as specified
        getOperationConstantList().add(1.5);
    }
    
    @Override
    protected double[] operation(double[] values) {
        // Get the operation constant
        double constant = getOperationConstantList().get(0);
        
        // Subtract the constant from the input value
        return new double[] { values[0] - constant };
    }

    @Override
    public SubtractionGene copyOf() {
        SubtractionGene copy = new SubtractionGene();
        cloneProperties(copy);
        return copy;
    }
}