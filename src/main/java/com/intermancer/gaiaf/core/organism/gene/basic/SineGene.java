package com.intermancer.gaiaf.core.organism.gene.basic;

/**
 * A Gene that applies the sine function to a DataPoint.
 */
public class SineGene extends BaseSingleDataPointGene {
    @Override
    protected double operation(double dataPointValue) {
        return Math.sin(dataPointValue);
    }
}