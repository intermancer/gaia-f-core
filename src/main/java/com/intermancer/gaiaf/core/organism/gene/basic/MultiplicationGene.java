package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.Gene;

/**
 * A Gene that multiplies a DataPoint by a constant value.
 */
public class MultiplicationGene extends Gene {
    
    public MultiplicationGene() {
        super();
        // Initialize operationConstantList with 1.5 as specified
        getOperationConstantList().add(1.5);
    }
    
    @Override
    protected double[] operation(double[] values) {
        // Get the operation constant
        double constant = getOperationConstantList().get(0);
        
        // Multiply the input value by the constant
        return new double[] { values[0] * constant };
    }

    @Override
    public MultiplicationGene copyOf() {
        MultiplicationGene copy = new MultiplicationGene();
        cloneProperties(copy);
        return copy;
    }
}