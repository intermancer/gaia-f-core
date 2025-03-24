package com.intermancer.gaiaf.core.organism.gene.basic;

/**
 * A Gene that divides a DataPoint by a constant value.
 */
public class DivisionGene extends BaseSingleDataPointGene {
    @Override
    protected double operation(double dataPointValue) {
        if (getAppliedConstant() == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return dataPointValue / getAppliedConstant();
    }
}