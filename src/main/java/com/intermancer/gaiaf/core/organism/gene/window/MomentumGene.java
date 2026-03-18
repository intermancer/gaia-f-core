package com.intermancer.gaiaf.core.organism.gene.window;

/**
 * A Window Gene that computes the net change over the buffer window.
 *
 * <p>{@code result = windowValues[last] - windowValues[first]}
 *
 * <p>Measures the net change over the window, giving a direct reading of
 * trend direction and speed.
 */
public class MomentumGene extends WindowGene {

    /**
     * Default constructor.
     */
    public MomentumGene() {
        super();
    }

    /**
     * Computes the difference between the most recent and oldest buffered values.
     *
     * @param windowValues Snapshot of the current buffer, oldest first.
     * @return A single-element array containing the momentum value.
     */
    @Override
    protected double[] windowOperation(double[] windowValues) {
        double oldest = windowValues[0];
        double newest = windowValues[windowValues.length - 1];
        return new double[] { newest - oldest };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MomentumGene copyOf() {
        MomentumGene copy = new MomentumGene();
        cloneProperties(copy);
        return copy;
    }
}
