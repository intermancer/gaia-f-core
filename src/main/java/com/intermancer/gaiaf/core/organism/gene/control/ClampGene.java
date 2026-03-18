package com.intermancer.gaiaf.core.organism.gene.control;

import java.util.ArrayList;
import java.util.List;

import com.intermancer.gaiaf.core.organism.Gene;

/**
 * A Control Gene that clamps a value between a dynamic floor and ceiling.
 *
 * <p>Reads three DataPoints identified by targetIndexList:
 * <ol>
 *   <li>value — the value to clamp</li>
 *   <li>floor — the minimum bound</li>
 *   <li>ceiling — the maximum bound</li>
 * </ol>
 *
 * <p>{@code result = min(ceiling, max(floor, value))}
 *
 * <p>By default, targetIndexList is initialized with -3, -2, and -1.
 */
public class ClampGene extends Gene {

    /**
     * Default constructor. Initializes targetIndexList with -3, -2, and -1.
     */
    public ClampGene() {
        super();
        List<Integer> indices = new ArrayList<>();
        indices.add(-3);
        indices.add(-2);
        indices.add(-1);
        setTargetIndexList(indices);
    }

    /**
     * Clamps values[0] between values[1] (floor) and values[2] (ceiling).
     *
     * @param values The three input values: value, floor, ceiling.
     * @return A single-element array containing the clamped value.
     */
    @Override
    protected double[] operation(double[] values) {
        double value = values[0];
        double floor = values[1];
        double ceiling = values[2];
        return new double[] { Math.min(ceiling, Math.max(floor, value)) };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClampGene copyOf() {
        ClampGene copy = new ClampGene();
        cloneProperties(copy);
        return copy;
    }
}
