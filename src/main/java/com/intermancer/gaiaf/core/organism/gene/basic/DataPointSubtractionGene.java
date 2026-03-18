package com.intermancer.gaiaf.core.organism.gene.basic;

import java.util.ArrayList;
import java.util.List;

import com.intermancer.gaiaf.core.organism.Gene;

/**
 * A Multi-DataPoint Gene that subtracts the second DataPoint value from the first.
 * Uses two indices in targetIndexList (-2 and -1 by default) to read two
 * DataPoints from the DataQuantum and appends their difference as a new DataPoint.
 */
public class DataPointSubtractionGene extends Gene {

    /**
     * Default constructor. Initializes targetIndexList with -2 and -1.
     */
    public DataPointSubtractionGene() {
        super();
        List<Integer> indices = new ArrayList<>();
        indices.add(-2);
        indices.add(-1);
        setTargetIndexList(indices);
    }

    /**
     * Subtracts the second DataPoint value from the first.
     *
     * @param values The two input values from the DataQuantum.
     * @return A single-element array containing the difference.
     */
    @Override
    protected double[] operation(double[] values) {
        return new double[] { values[0] - values[1] };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataPointSubtractionGene copyOf() {
        DataPointSubtractionGene copy = new DataPointSubtractionGene();
        cloneProperties(copy);
        return copy;
    }
}
