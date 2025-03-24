package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import com.intermancer.gaiaf.core.organism.Gene;

/**
 * Abstract base class for Single-DataPoint Genes.
 * Provides common functionality for Genes that operate on a single DataPoint.
 */
public abstract class BaseSingleDataPointGene extends Gene {
    private int index;
    private double appliedConstant;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getAppliedConstant() {
        return appliedConstant;
    }

    public void setAppliedConstant(double appliedConstant) {
        this.appliedConstant = appliedConstant;
    }

    @Override
    public void consume(DataQuantum dataQuantum) {
        DataQuantum.DataPoint dataPoint = dataQuantum.getDataPoint(index);
        double result = operation(dataPoint.getValue());
        dataQuantum.addDataPoint(new DataQuantum.DataPoint(getId(), result));
    }

    @Override
    public String getId() {
        return this.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[index=" + index + ", appliedConstant=" + appliedConstant + "]";
    }

    /**
     * Abstract method to define the operation performed on the DataPoint value.
     *
     * @param dataPointValue The value of the DataPoint.
     * @return The result of the operation.
     */
    protected abstract double operation(double dataPointValue);
}