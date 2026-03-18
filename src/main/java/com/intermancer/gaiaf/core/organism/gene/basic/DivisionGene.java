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
        double constant = getOperationConstantList().get(0);
        if (constant == 0) {
            return new double[] { 0.0 };
        }
        return new double[] { values[0] / constant };
    }

    @Override
    public DivisionGene copyOf() {
        DivisionGene copy = new DivisionGene();
        cloneProperties(copy);
        return copy;
    }
}