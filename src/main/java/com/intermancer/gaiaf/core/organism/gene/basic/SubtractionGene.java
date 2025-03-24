package com.intermancer.gaiaf.core.organism.gene.basic;

/**
 * A Gene that subtracts a constant value from a DataPoint.
 */
public class SubtractionGene extends BaseSingleDataPointGene {
    @Override
    protected double operation(double dataPointValue) {
        return dataPointValue - getAppliedConstant();
    }
}