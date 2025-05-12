package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.Gene;

/**
 * A Gene that divides a DataPoint by a constant value.
 */
public class DivisionGene extends Gene {
    
    public DivisionGene() {
        super();
        // Initialize operationConstantList with 1.5 as specified
        getOperationConstantList().add(1.5);
    }
    
    @Override
    protected double[] operation(double[] values) {
        // Get the operation constant
        double constant = getOperationConstantList().get(0);
        
        // Check for division by zero
        if (constant == 0) {
            throw new ArithmeticException("Division by zero");
        }
        
        // Divide the input value by the constant
        return new double[] { values[0] / constant };
    }

    @Override
    public DivisionGene copyOf() {
        DivisionGene copy = new DivisionGene();
        cloneProperties(copy);
        return copy;
    }
}