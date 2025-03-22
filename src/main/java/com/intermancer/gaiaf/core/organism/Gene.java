package com.intermancer.gaiaf.core.organism;

/**
 * Represents a Gene, which is a DataQuantumConsumer.
 * A Gene processes a DataQuantum by retrieving values, performing
 * operations on them, and adding new values back into the DataQuantum.
 */
public abstract class Gene implements DataQuantumConsumer {

    /**
     * Processes the given DataQuantum. A Gene retrieves one or more values
     * from the DataQuantum, performs operations on them, and adds one or
     * more values back into the DataQuantum.
     *
     * @param dataQuantum The DataQuantum to process.
     */
    @Override
    public abstract void consume(DataQuantum dataQuantum);

    /**
     * Returns a unique identifier for the Gene.
     *
     * @return A unique ID as a String.
     */
    @Override
    public abstract String getId();
}