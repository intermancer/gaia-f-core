package com.intermancer.gaiaf.core.organism;

public abstract class Gene implements DataQuantumConsumer {
    @Override
    public abstract void consume(DataQuantum dataQuantum);
}