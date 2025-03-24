package com.intermancer.gaiaf.core.organism.gene.basic;

/**
 * A Gene that adds a constant value to a DataPoint.
 */
public class AdditionGene extends BaseSingleDataPointGene {
    @Override
    protected double operation(double dataPointValue) {
        return dataPointValue + getAppliedConstant();
    }
}