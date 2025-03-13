package com.intermancer.gaiaf.core.organism;

import java.util.ArrayList;
import java.util.List;

public class Organism implements DataQuantumConsumer {
    protected List<Chromosome> chromosomes = new ArrayList<>();

    @Override
    public void consume(DataQuantum dataQuantum) {
        for (Chromosome chromosome : chromosomes) {
            chromosome.consume(dataQuantum);
        }
    }
}