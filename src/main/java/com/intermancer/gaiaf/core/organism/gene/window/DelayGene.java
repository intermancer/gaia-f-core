package com.intermancer.gaiaf.core.organism.gene.window;

/**
 * A Window Gene that emits the oldest value in the buffer — the value from N steps ago.
 *
 * <p>{@code result = windowValues[first]}
 *
 * <p>Allows an organism to compare the current state of a channel against its own
 * past, enabling pattern detection across time.
 */
public class DelayGene extends WindowGene {

    /**
     * Default constructor.
     */
    public DelayGene() {
        super();
    }

    /**
     * Returns the oldest value currently in the buffer.
     *
     * @param windowValues Snapshot of the current buffer, oldest first.
     * @return A single-element array containing the delayed value.
     */
    @Override
    protected double[] windowOperation(double[] windowValues) {
        return new double[] { windowValues[0] };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DelayGene copyOf() {
        DelayGene copy = new DelayGene();
        cloneProperties(copy);
        return copy;
    }
}
