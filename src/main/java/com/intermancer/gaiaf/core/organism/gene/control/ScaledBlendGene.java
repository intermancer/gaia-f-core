package com.intermancer.gaiaf.core.organism.gene.control;

import java.util.ArrayList;
import java.util.List;

import com.intermancer.gaiaf.core.organism.Gene;

/**
 * A Control Gene that blends two values using a data-driven mix ratio.
 *
 * <p>Reads three DataPoints identified by targetIndexList:
 * <ol>
 *   <li>value1 — first value to blend</li>
 *   <li>value2 — second value to blend</li>
 *   <li>weight — blend ratio, clamped to [0.0, 1.0]</li>
 * </ol>
 *
 * <p>{@code result = weight * value1 + (1 - weight) * value2}
 *
 * <p>By default, targetIndexList is initialized with -3, -2, and -1.
 */
public class ScaledBlendGene extends Gene {

    /**
     * Default constructor. Initializes targetIndexList with -3, -2, and -1.
     */
    public ScaledBlendGene() {
        super();
        List<Integer> indices = new ArrayList<>();
        indices.add(-3);
        indices.add(-2);
        indices.add(-1);
        setTargetIndexList(indices);
    }

    /**
     * Blends value1 and value2 using the third DataPoint as the mix ratio.
     *
     * @param values The three input values: value1, value2, weight.
     * @return A single-element array containing the blended value.
     */
    @Override
    protected double[] operation(double[] values) {
        double value1 = values[0];
        double value2 = values[1];
        double weight = Math.min(1.0, Math.max(0.0, values[2]));
        return new double[] { weight * value1 + (1.0 - weight) * value2 };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScaledBlendGene copyOf() {
        ScaledBlendGene copy = new ScaledBlendGene();
        cloneProperties(copy);
        return copy;
    }
}
