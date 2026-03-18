package com.intermancer.gaiaf.core.organism.gene.control;

import java.util.ArrayList;
import java.util.List;

import com.intermancer.gaiaf.core.organism.Gene;

/**
 * A Control Gene that multiplies a value by the sign of a polarity control signal.
 *
 * <p>Reads two DataPoints identified by targetIndexList:
 * <ol>
 *   <li>value — the value whose polarity may be flipped</li>
 *   <li>control — the signal whose sign determines polarity</li>
 * </ol>
 *
 * <p>{@code result = value * sign(control)}
 *
 * <p>Where {@code sign(x)} is +1.0 if x &gt; 0, -1.0 if x &lt; 0, and 0.0 if x == 0.
 *
 * <p>By default, targetIndexList is initialized with -2 and -1.
 */
public class MagnitudeGateGene extends Gene {

    /**
     * Default constructor. Initializes targetIndexList with -2 and -1.
     */
    public MagnitudeGateGene() {
        super();
        List<Integer> indices = new ArrayList<>();
        indices.add(-2);
        indices.add(-1);
        setTargetIndexList(indices);
    }

    /**
     * Multiplies the value by the sign of the control signal.
     *
     * @param values The two input values: value, control.
     * @return A single-element array containing the result.
     */
    @Override
    protected double[] operation(double[] values) {
        double value = values[0];
        double control = values[1];
        double sign = Double.compare(control, 0.0) > 0 ? 1.0
                    : Double.compare(control, 0.0) < 0 ? -1.0
                    : 0.0;
        return new double[] { value * sign };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MagnitudeGateGene copyOf() {
        MagnitudeGateGene copy = new MagnitudeGateGene();
        cloneProperties(copy);
        return copy;
    }
}
