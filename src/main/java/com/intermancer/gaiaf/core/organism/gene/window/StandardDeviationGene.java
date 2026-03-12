package com.intermancer.gaiaf.core.organism.gene.window;

/**
 * A Window Gene that computes the population standard deviation of the buffer.
 *
 * <p>{@code result = sqrt( sum( (x - mean)^2 ) / N )}
 */
public class StandardDeviationGene extends WindowGene {

    /**
     * Default constructor.
     */
    public StandardDeviationGene() {
        super();
    }

    /**
     * Computes the population standard deviation of the buffered values.
     *
     * @param windowValues Snapshot of the current buffer, oldest first.
     * @return A single-element array containing the standard deviation.
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

        return new double[] { Math.sqrt(variance) };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StandardDeviationGene copyOf() {
        StandardDeviationGene copy = new StandardDeviationGene();
        cloneProperties(copy);
        return copy;
    }
}
