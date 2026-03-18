package com.intermancer.gaiaf.core.organism.gene.window;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import com.intermancer.gaiaf.core.organism.Gene;

/**
 * Abstract base class for all Window Genes.
 *
 * <p>A WindowGene maintains an internal rolling buffer of the last N values
 * sampled from a single DataPoint channel. On each call to {@code consume()},
 * it reads the value at the target index, appends it to the buffer (dropping
 * the oldest value once the buffer exceeds N), then delegates to
 * {@link #windowOperation(double[])} to compute and append a result.
 *
 * <p>The window size N is stored as the first entry in {@code operationConstantList}
 * (default 5.0, interpreted as an integer), making it mutable via the standard
 * Gene mutation infrastructure.
 *
 * <p>During the warm-up period (fewer than N values buffered), the Gene operates
 * on however many values are currently available rather than skipping or emitting
 * a sentinel.
 */
public abstract class WindowGene extends Gene {

    /** Default window size. */
    private static final double DEFAULT_WINDOW_SIZE = 5.0;

    /** Rolling buffer of sampled values. */
    private Deque<Double> buffer;

    /**
     * Default constructor. Initializes the window size constant to 5.0 and
     * creates an empty buffer.
     */
    public WindowGene() {
        super();
        getOperationConstantList().add(DEFAULT_WINDOW_SIZE);
        buffer = new ArrayDeque<>();
    }

    /**
     * Returns the window size N, derived from the first entry in
     * {@code operationConstantList}, cast to a positive integer.
     * A minimum of 1 is enforced.
     *
     * @return The window size.
     */
    protected int getWindowSize() {
        double raw = getOperationConstantList().get(0);
        return Math.max(1, (int) Math.abs(raw));
    }

    /**
     * Returns the window size as the number of warming cycles required before
     * this Gene's output is fully meaningful.
     *
     * @return The window size N.
     */
    @Override
    public int getWarmingCycles() {
        return getWindowSize();
    }

    /**
     * Overrides the Gene template method to maintain the rolling buffer before
     * delegating to {@link #windowOperation(double[])}.
     *
     * @param dataQuantum The DataQuantum to process.
     */
    @Override
    public void consume(DataQuantum dataQuantum) {
        int targetIndex = getTargetIndexList().get(0);
        double newValue = dataQuantum.getValue(targetIndex);

        // Append new value; evict oldest if buffer exceeds window size
        buffer.addLast(newValue);
        while (buffer.size() > getWindowSize()) {
            buffer.removeFirst();
        }

        // Build array snapshot for subclass
        double[] windowValues = buffer.stream().mapToDouble(Double::doubleValue).toArray();

        double[] results = windowOperation(windowValues);

        for (double result : results) {
            dataQuantum.addDataPoint(new DataQuantum.DataPoint(getId(), result));
        }
    }

    /**
     * Performs the window-based operation on the buffered values.
     * Subclasses implement this instead of {@code operation()}.
     *
     * @param windowValues Snapshot of the current buffer contents, oldest first.
     * @return The results to append as new DataPoints.
     */
    protected abstract double[] windowOperation(double[] windowValues);

    /**
     * Not used by WindowGene; window logic is handled in {@link #consume(DataQuantum)}.
     * Subclasses must not rely on this method being called.
     *
     * @param values Unused.
     * @return Empty array.
     */
    @Override
    protected final double[] operation(double[] values) {
        return new double[0];
    }

    /**
     * Deep-copies the buffer in addition to the standard Gene properties,
     * so a cloned Gene begins with the same accumulated state.
     *
     * @param clone The WindowGene clone to copy properties into.
     */
    @Override
    protected void cloneProperties(Gene clone) {
        super.cloneProperties(clone);
        if (clone instanceof WindowGene windowClone) {
            windowClone.buffer = new ArrayDeque<>(this.buffer);
        }
    }

    /**
     * Returns the current buffer contents as an unmodifiable list.
     * Exposed for testing purposes.
     *
     * @return A list of buffered values, oldest first.
     */
    protected List<Double> getBuffer() {
        return new ArrayList<>(buffer);
    }
}
