package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.Gene;

/**
 * A Gene that applies the sine function to a DataPoint.
 */
public class SineGene extends Gene {
    
    @Override
    protected double[] operation(double[] values) {
        // Apply sine function to the input value
        return new double[] { Math.sin(values[0]) };
    }
}