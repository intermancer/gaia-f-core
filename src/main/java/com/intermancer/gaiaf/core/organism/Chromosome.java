package com.intermancer.gaiaf.core.organism;

import java.util.ArrayList;
import java.util.List;

public class Chromosome implements DataQuantumConsumer {
    protected List<Gene> genes = new ArrayList<>();

    @Override
    public void consume(DataQuantum dataQuantum) {
        for (Gene gene : genes) {
            gene.consume(dataQuantum);
        }
    }
}