package com.intermancer.gaiaf.core.organism.gene.control;

import java.util.ArrayList;
import java.util.List;

import com.intermancer.gaiaf.core.organism.Gene;

/**
 * A Control Gene that selects between two values based on a control signal and threshold.
 *
 * <p>Reads three DataPoints identified by targetIndexList:
 * <ol>
 *   <li>value1 — output when control exceeds the threshold</li>
 *   <li>value2 — output when control does not exceed the threshold</li>
 *   <li>control — the signal compared against the threshold</li>
 * </ol>
 *
 * <p>The threshold is stored in {@code operationConstantList[0]}, initialized to 0.0.
 *
 * <p>{@code result = (control > threshold) ? value1 : value2}
 *
 * <p>By default, targetIndexList is initialized with -3, -2, and -1.
 */
public class ThresholdSwitchGene extends Gene {

    /**
     * Default constructor. Initializes targetIndexList with -3, -2, -1 and
     * threshold constant to 0.0.
     */
    public ThresholdSwitchGene() {
        super();
        List<Integer> indices = new ArrayList<>();
        indices.add(-3);
        indices.add(-2);
        indices.add(-1);
        setTargetIndexList(indices);
        getOperationConstantList().add(0.0);
    }

    /**
     * Returns value1 if the control signal exceeds the threshold, otherwise value2.
     *
     * @param values The three input values: value1, value2, control.
     * @return A single-element array containing the selected value.
     */
    @Override
    protected double[] operation(double[] values) {
        double value1 = values[0];
        double value2 = values[1];
        double control = values[2];
        double threshold = getOperationConstantList().get(0);
        return new double[] { control > threshold ? value1 : value2 };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ThresholdSwitchGene copyOf() {
        ThresholdSwitchGene copy = new ThresholdSwitchGene();
        cloneProperties(copy);
        return copy;
    }
}
