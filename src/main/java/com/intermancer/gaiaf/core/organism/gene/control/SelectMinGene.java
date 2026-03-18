package com.intermancer.gaiaf.core.organism.gene.control;

import java.util.ArrayList;
import java.util.List;

import com.intermancer.gaiaf.core.organism.Gene;

/**
 * A Control Gene that outputs the smaller of two DataPoint values.
 *
 * <p>{@code result = min(value1, value2)}
 *
 * <p>By default, targetIndexList is initialized with -2 and -1.
 */
public class SelectMinGene extends Gene {

    /**
     * Default constructor. Initializes targetIndexList with -2 and -1.
     */
    public SelectMinGene() {
        super();
        List<Integer> indices = new ArrayList<>();
        indices.add(-2);
        indices.add(-1);
        setTargetIndexList(indices);
    }

    /**
     * Returns the smaller of the two input values.
     *
     * @param values The two input values.
     * @return A single-element array containing the minimum.
     */
    @Override
    protected double[] operation(double[] values) {
        return new double[] { Math.min(values[0], values[1]) };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SelectMinGene copyOf() {
        SelectMinGene copy = new SelectMinGene();
        cloneProperties(copy);
        return copy;
    }
}
