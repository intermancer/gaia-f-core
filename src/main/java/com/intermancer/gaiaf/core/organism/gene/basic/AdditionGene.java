package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.Gene;

/**
 * A Gene that adds a constant value to a DataPoint.
 */
public class AdditionGene extends Gene {
    
    public AdditionGene() {
        super();
        // Initialize operationConstantList with 1.5 as specified
        getOperationConstantList().add(1.5);
    }
    
    @Override
    protected double[] operation(double[] values) {
        // Get the operation constant
        double constant = getOperationConstantList().get(0);
        
        // Add the constant to the input value
        return new double[] { values[0] + constant };
    }

    @Override
    public AdditionGene copyOf() {
        AdditionGene copy = new AdditionGene();
        cloneProperties(copy);
        return copy;
    }
}