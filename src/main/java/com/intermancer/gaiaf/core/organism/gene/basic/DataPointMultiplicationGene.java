package com.intermancer.gaiaf.core.organism.gene.basic;

import java.util.ArrayList;
import java.util.List;

import com.intermancer.gaiaf.core.organism.Gene;

/**
 * A Multi-DataPoint Gene that multiplies two DataPoint values.
 * Uses two indices in targetIndexList (-2 and -1 by default) to read two
 * DataPoints from the DataQuantum and appends their product as a new DataPoint.
 */
public class DataPointMultiplicationGene extends Gene {

    /**
     * Default constructor. Initializes targetIndexList with -2 and -1.
     */
    public DataPointMultiplicationGene() {
        super();
        List<Integer> indices = new ArrayList<>();
        indices.add(-2);
        indices.add(-1);
        setTargetIndexList(indices);
    }

    /**
     * Multiplies the first DataPoint value by the second.
     *
     * @param values The two input values from the DataQuantum.
     * @return A single-element array containing the product.
     */
    @Override
    protected double[] operation(double[] values) {
        return new double[] { values[0] * values[1] };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataPointMultiplicationGene copyOf() {
        DataPointMultiplicationGene copy = new DataPointMultiplicationGene();
        cloneProperties(copy);
        return copy;
    }
}
