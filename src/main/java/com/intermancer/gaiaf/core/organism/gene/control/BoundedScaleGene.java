package com.intermancer.gaiaf.core.organism.gene.control;

import java.util.ArrayList;
import java.util.List;

import com.intermancer.gaiaf.core.organism.Gene;

/**
 * A Control Gene that scales a value by a second DataPoint, clamping the scale
 * factor to a constant range to prevent runaway outputs.
 *
 * <p>Reads two DataPoints identified by targetIndexList:
 * <ol>
 *   <li>value — the value to scale</li>
 *   <li>scale — the scale factor, clamped to [-bound, +bound]</li>
 * </ol>
 *
 * <p>The bound is stored in {@code operationConstantList[0]}, initialized to 2.0.
 *
 * <p>{@code result = value * clamp(scale, -bound, bound)}
 *
 * <p>By default, targetIndexList is initialized with -2 and -1.
 */
public class BoundedScaleGene extends Gene {

    /**
     * Default constructor. Initializes targetIndexList with -2, -1 and bound to 2.0.
     */
    public BoundedScaleGene() {
        super();
        List<Integer> indices = new ArrayList<>();
        indices.add(-2);
        indices.add(-1);
        setTargetIndexList(indices);
        getOperationConstantList().add(2.0);
    }

    /**
     * Scales the value by the clamped scale factor.
     *
     * @param values The two input values: value, scale.
     * @return A single-element array containing the bounded scaled value.
     */
    @Override
    protected double[] operation(double[] values) {
        double value = values[0];
        double scale = values[1];
        double bound = Math.abs(getOperationConstantList().get(0));
        double clampedScale = Math.min(bound, Math.max(-bound, scale));
        return new double[] { value * clampedScale };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BoundedScaleGene copyOf() {
        BoundedScaleGene copy = new BoundedScaleGene();
        cloneProperties(copy);
        return copy;
    }
}
