package com.intermancer.gaiaf.core.organism.gene.control;

import com.intermancer.gaiaf.core.organism.Gene;

/**
 * A Control Gene that converts a continuous value into a discrete directional signal.
 *
 * <p>Reads one DataPoint identified by targetIndexList. The magnitude of the output
 * is stored in {@code operationConstantList[0]}, initialized to 1.0.
 *
 * <p>{@code result = (value > 0) ? magnitude : (value < 0) ? -magnitude : 0.0}
 */
public class SignGene extends Gene {

    /**
     * Default constructor. Initializes magnitude constant to 1.0.
     */
    public SignGene() {
        super();
        getOperationConstantList().add(1.0);
    }

    /**
     * Returns +magnitude, -magnitude, or 0.0 based on the sign of the input value.
     *
     * @param values The input value.
     * @return A single-element array containing the signed magnitude.
     */
    @Override
    protected double[] operation(double[] values) {
        double value = values[0];
        double magnitude = getOperationConstantList().get(0);
        if (value > 0.0) {
            return new double[] { magnitude };
        } else if (value < 0.0) {
            return new double[] { -magnitude };
        } else {
            return new double[] { 0.0 };
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SignGene copyOf() {
        SignGene copy = new SignGene();
        cloneProperties(copy);
        return copy;
    }
}
