package com.intermancer.gaiaf.core.organism.gene.basic;

/**
 * A Gene that multiplies a DataPoint by a constant value.
 */
public class MultiplicationGene extends BaseSingleDataPointGene {
    @Override
    protected double operation(double dataPointValue) {
        return dataPointValue * getAppliedConstant();
    }
}