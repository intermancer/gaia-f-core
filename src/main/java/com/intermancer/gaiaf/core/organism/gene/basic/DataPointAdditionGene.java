package com.intermancer.gaiaf.core.organism.gene.basic;

import java.util.ArrayList;
import java.util.List;

import com.intermancer.gaiaf.core.organism.Gene;

/**
 * A Multi-DataPoint Gene that adds two DataPoint values together.
 * Uses two indices in targetIndexList (-2 and -1 by default) to read two
 * DataPoints from the DataQuantum and appends their sum as a new DataPoint.
 */
public class DataPointAdditionGene extends Gene {

    /**
     * Default constructor. Initializes targetIndexList with -2 and -1.
     */
    public DataPointAdditionGene() {
        super();
        List<Integer> indices = new ArrayList<>();
        indices.add(-2);
        indices.add(-1);
        setTargetIndexList(indices);
    }

    /**
     * Adds the second DataPoint value to the first.
     *
     * @param values The two input values from the DataQuantum.
     * @return A single-element array containing the sum.
     */
    @Override
    protected double[] operation(double[] values) {
        return new double[] { values[0] + values[1] };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataPointAdditionGene copyOf() {
        DataPointAdditionGene copy = new DataPointAdditionGene();
        cloneProperties(copy);
        return copy;
    }
}
