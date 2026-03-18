package com.intermancer.gaiaf.core.organism.gene.window;

/**
 * A Window Gene that computes the arithmetic mean of the values in the buffer.
 *
 * <p>{@code result = sum(windowValues) / windowValues.length}
 */
public class MovingAverageGene extends WindowGene {

    /**
     * Default constructor.
     */
    public MovingAverageGene() {
        super();
    }

    /**
     * Computes the arithmetic mean of the buffered values.
     *
     * @param windowValues Snapshot of the current buffer, oldest first.
     * @return A single-element array containing the mean.
     */
    @Override
    protected double[] windowOperation(double[] windowValues) {
        double sum = 0.0;
        for (double v : windowValues) {
            sum += v;
        }
        return new double[] { sum / windowValues.length };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MovingAverageGene copyOf() {
        MovingAverageGene copy = new MovingAverageGene();
        cloneProperties(copy);
        return copy;
    }
}
