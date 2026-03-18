package com.intermancer.gaiaf.core.organism.gene.control;

import java.util.ArrayList;
import java.util.List;

import com.intermancer.gaiaf.core.organism.Gene;

/**
 * A Control Gene that passes a value only when the absolute value of a gate
 * signal exceeds a threshold; otherwise outputs 0.0.
 *
 * <p>Reads two DataPoints identified by targetIndexList:
 * <ol>
 *   <li>value — the value to pass through or suppress</li>
 *   <li>gate — the signal whose absolute value controls the gate</li>
 * </ol>
 *
 * <p>The threshold is stored in {@code operationConstantList[0]}, initialized to 1.0.
 *
 * <p>{@code result = (|gate| > threshold) ? value : 0.0}
 *
 * <p>By default, targetIndexList is initialized with -2 and -1.
 */
public class AbsoluteValueGateGene extends Gene {

    /**
     * Default constructor. Initializes targetIndexList with -2, -1 and
     * threshold constant to 1.0.
     */
    public AbsoluteValueGateGene() {
        super();
        List<Integer> indices = new ArrayList<>();
        indices.add(-2);
        indices.add(-1);
        setTargetIndexList(indices);
        getOperationConstantList().add(1.0);
    }

    /**
     * Passes value through if |gate| exceeds threshold; otherwise returns 0.0.
     *
     * @param values The two input values: value, gate.
     * @return A single-element array containing the gated value.
     */
    @Override
    protected double[] operation(double[] values) {
        double value = values[0];
        double gate = values[1];
        double threshold = getOperationConstantList().get(0);
        return new double[] { Math.abs(gate) > threshold ? value : 0.0 };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbsoluteValueGateGene copyOf() {
        AbsoluteValueGateGene copy = new AbsoluteValueGateGene();
        cloneProperties(copy);
        return copy;
    }
}
