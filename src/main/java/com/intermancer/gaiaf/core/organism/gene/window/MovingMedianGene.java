package com.intermancer.gaiaf.core.organism.gene.window;

import java.util.Arrays;

/**
 * A Window Gene that computes the median of the values in the buffer.
 *
 * <p>For an odd-length buffer, returns the middle value of the sorted buffer.
 * For an even-length buffer, returns the average of the two middle values.
 */
public class MovingMedianGene extends WindowGene {

    /**
     * Default constructor.
     */
    public MovingMedianGene() {
        super();
    }

    /**
     * Computes the median of the buffered values.
     *
     * @param windowValues Snapshot of the current buffer, oldest first.
     * @return A single-element array containing the median.
     */
    @Override
    protected double[] windowOperation(double[] windowValues) {
        double[] sorted = Arrays.copyOf(windowValues, windowValues.length);
        Arrays.sort(sorted);
        int mid = sorted.length / 2;
        double median;
        if (sorted.length % 2 == 1) {
            median = sorted[mid];
        } else {
            median = (sorted[mid - 1] + sorted[mid]) / 2.0;
        }
        return new double[] { median };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MovingMedianGene copyOf() {
        MovingMedianGene copy = new MovingMedianGene();
        cloneProperties(copy);
        return copy;
    }
}
