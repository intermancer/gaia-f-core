package com.intermancer.gaiaf.core.organism.gene.window;

/**
 * A Window Gene that computes the range (max - min) of the values in the buffer.
 *
 * <p>{@code result = max(windowValues) - min(windowValues)}
 *
 * <p>A simple volatility measure; a large range indicates high variability,
 * a small range indicates consolidation.
 */
public class RangeGene extends WindowGene {

    /**
     * Default constructor.
     */
    public RangeGene() {
        super();
    }

    /**
     * Computes the range of the buffered values.
     *
     * @param windowValues Snapshot of the current buffer, oldest first.
     * @return A single-element array containing the range.
     */
    @Override
    protected double[] windowOperation(double[] windowValues) {
        double min = windowValues[0];
        double max = windowValues[0];
        for (double v : windowValues) {
            if (v < min) min = v;
            if (v > max) max = v;
        }
        return new double[] { max - min };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RangeGene copyOf() {
        RangeGene copy = new RangeGene();
        cloneProperties(copy);
        return copy;
    }
}
