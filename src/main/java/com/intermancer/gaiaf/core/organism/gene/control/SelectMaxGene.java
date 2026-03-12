package com.intermancer.gaiaf.core.organism.gene.control;

import java.util.ArrayList;
import java.util.List;

import com.intermancer.gaiaf.core.organism.Gene;

/**
 * A Control Gene that outputs the larger of two DataPoint values.
 *
 * <p>{@code result = max(value1, value2)}
 *
 * <p>By default, targetIndexList is initialized with -2 and -1.
 */
public class SelectMaxGene extends Gene {

    /**
     * Default constructor. Initializes targetIndexList with -2 and -1.
     */
    public SelectMaxGene() {
        super();
        List<Integer> indices = new ArrayList<>();
        indices.add(-2);
        indices.add(-1);
        setTargetIndexList(indices);
    }

    /**
     * Returns the larger of the two input values.
     *
     * @param values The two input values.
     * @return A single-element array containing the maximum.
     */
    @Override
    protected double[] operation(double[] values) {
        return new double[] { Math.max(values[0], values[1]) };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SelectMaxGene copyOf() {
        SelectMaxGene copy = new SelectMaxGene();
        cloneProperties(copy);
        return copy;
    }
}
