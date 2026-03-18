package com.intermancer.gaiaf.core.organism.gene.window;

/**
 * A Window Gene that fits a least-squares linear regression line to the buffer
 * and projects it one step forward.
 *
 * <p>{@code result = slope * (N + 1) + intercept}
 *
 * <p>Buffer positions are treated as the x-axis (1-based) and buffer values as y.
 * The slope and intercept are derived from standard least-squares formulas.
 */
public class LinearProjectionGene extends WindowGene {

    /**
     * Default constructor.
     */
    public LinearProjectionGene() {
        super();
    }

    /**
     * Fits a least-squares line to the buffered values and projects one step ahead.
     *
     * @param windowValues Snapshot of the current buffer, oldest first.
     * @return A single-element array containing the projected value.
     */
    @Override
    protected double[] windowOperation(double[] windowValues) {
        int n = windowValues.length;

        if (n == 1) {
            return new double[] { windowValues[0] };
        }

        // x values are 1-based positions: 1, 2, ..., n
        double sumX = 0.0;
        double sumY = 0.0;
        double sumXY = 0.0;
        double sumX2 = 0.0;

        for (int i = 0; i < n; i++) {
            double x = i + 1.0;
            double y = windowValues[i];
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double denominator = n * sumX2 - sumX * sumX;
        double slope = (denominator == 0.0) ? 0.0 : (n * sumXY - sumX * sumY) / denominator;
        double intercept = (sumY - slope * sumX) / n;

        double projection = slope * (n + 1) + intercept;
        return new double[] { projection };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinearProjectionGene copyOf() {
        LinearProjectionGene copy = new LinearProjectionGene();
        cloneProperties(copy);
        return copy;
    }
}
