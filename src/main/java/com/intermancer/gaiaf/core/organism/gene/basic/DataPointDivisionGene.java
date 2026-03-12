package com.intermancer.gaiaf.core.organism.gene.basic;

import java.util.ArrayList;
import java.util.List;

import com.intermancer.gaiaf.core.organism.Gene;

/**
 * A Multi-DataPoint Gene that divides the first DataPoint value by the second.
 * Uses two indices in targetIndexList (-2 and -1 by default) to read two
 * DataPoints from the DataQuantum and appends their quotient as a new DataPoint.
 * If the divisor is zero, outputs 0.0.
 */
public class DataPointDivisionGene extends Gene {

    /**
     * Default constructor. Initializes targetIndexList with -2 and -1.
     */
    public DataPointDivisionGene() {
        super();
        List<Integer> indices = new ArrayList<>();
        indices.add(-2);
        indices.add(-1);
        setTargetIndexList(indices);
    }

    /**
     * Divides the first DataPoint value by the second.
     * Returns 0.0 if the divisor is zero.
     *
     * @param values The two input values from the DataQuantum.
     * @return A single-element array containing the quotient, or 0.0 if the divisor is zero.
     */
    @Override
    protected double[] operation(double[] values) {
        if (values[1] == 0.0) {
            return new double[] { 0.0 };
        }
        return new double[] { values[0] / values[1] };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataPointDivisionGene copyOf() {
        DataPointDivisionGene copy = new DataPointDivisionGene();
        cloneProperties(copy);
        return copy;
    }
}
