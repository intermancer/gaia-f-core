package com.intermancer.gaiaf.core.organism;

/**
 * Represents a consumer of DataQuantum objects.
 * This interface ensures that implementing classes can process
 * a DataQuantum and provide a unique identifier.
 */
public interface DataQuantumConsumer {
    /**
     * Consumes a DataQuantum, performing operations on it.
     *
     * @param dataQuantum The DataQuantum to consume.
     */
    void consume(DataQuantum dataQuantum);
}