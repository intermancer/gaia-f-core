package com.intermancer.gaiaf.core.organism.gene.window;

/**
 * A Window Gene that normalizes the most recent value relative to the window's
 * mean and standard deviation.
 *
 * <p>{@code result = (windowValues[last] - mean) / standardDeviation}
 *
 * <p>Returns 0.0 if the standard deviation of the window is zero.
 */
public class ZScoreGene extends WindowGene {

    /**
     * Default constructor.
     */
    public ZScoreGene() {
        super();
    }

    /**
     * Computes the z-score of the most recent buffered value.
     *
     * @param windowValues Snapshot of the current buffer, oldest first.
     * @return A single-element array containing the z-score.
     */
    @Override
    protected double[] windowOperation(double[] windowValues) {
        double mean = 0.0;
        for (double v : windowValues) {
            mean += v;
        }
        mean /= windowValues.length;

        double variance = 0.0;
        for (double v : windowValues) {
            double diff = v - mean;
            variance += diff * diff;
        }
        variance /= windowValues.length;
        double stdDev = Math.sqrt(variance);

        if (stdDev == 0.0) {
            return new double[] { 0.0 };
        }

        double newest = windowValues[windowValues.length - 1];
        return new double[] { (newest - mean) / stdDev };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ZScoreGene copyOf() {
        ZScoreGene copy = new ZScoreGene();
        cloneProperties(copy);
        return copy;
    }
}
