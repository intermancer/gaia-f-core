package com.intermancer.gaiaf.core.organism.gene.window;

import com.intermancer.gaiaf.core.organism.Gene;

/**
 * A Window Gene that computes an exponentially weighted moving average (EMA).
 *
 * <p>{@code ema = alpha * currentValue + (1 - alpha) * previousEma}
 *
 * <p>The window size N is stored as the first entry in {@code operationConstantList}
 * (inherited from {@link WindowGene}). The decay factor alpha is stored as the
 * second entry, initialized to 0.2. On the first call, {@code previousEma} is
 * initialized to the first buffered value.
 *
 * <p>Because alpha is in {@code operationConstantList}, it mutates independently
 * of the window size, giving evolution two separate parameters to tune.
 */
public class ExponentialMovingAverageGene extends WindowGene {

    private static final double DEFAULT_ALPHA = 0.2;

    /** The EMA value from the previous consume() call. */
    private double previousEma;

    /** Whether the EMA has been initialized. */
    private boolean initialized;

    /**
     * Default constructor. Adds alpha (0.2) as the second operationConstantList entry.
     */
    public ExponentialMovingAverageGene() {
        super();
        getOperationConstantList().add(DEFAULT_ALPHA);
        initialized = false;
        previousEma = 0.0;
    }

    /**
     * Returns the alpha decay factor, clamped to (0.0, 1.0].
     *
     * @return The alpha value.
     */
    protected double getAlpha() {
        double raw = getOperationConstantList().get(1);
        // Clamp to (0, 1] so EMA is always meaningful
        return Math.min(1.0, Math.max(0.001, Math.abs(raw)));
    }

    /**
     * Computes the EMA using the most recent buffered value.
     *
     * @param windowValues Snapshot of the current buffer, oldest first.
     * @return A single-element array containing the EMA.
     */
    @Override
    protected double[] windowOperation(double[] windowValues) {
        double current = windowValues[windowValues.length - 1];
        double alpha = getAlpha();

        if (!initialized) {
            previousEma = current;
            initialized = true;
        } else {
            previousEma = alpha * current + (1.0 - alpha) * previousEma;
        }

        return new double[] { previousEma };
    }

    /**
     * Deep-copies the EMA state in addition to the WindowGene buffer.
     *
     * @param clone The Gene clone to copy properties into.
     */
    @Override
    protected void cloneProperties(Gene clone) {
        super.cloneProperties(clone);
        if (clone instanceof ExponentialMovingAverageGene emaClone) {
            emaClone.previousEma = this.previousEma;
            emaClone.initialized = this.initialized;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExponentialMovingAverageGene copyOf() {
        ExponentialMovingAverageGene copy = new ExponentialMovingAverageGene();
        cloneProperties(copy);
        return copy;
    }
}
