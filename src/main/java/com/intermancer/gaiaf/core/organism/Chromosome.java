package com.intermancer.gaiaf.core.organism;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Chromosome, which is an ordered list of Genes.
 * A Chromosome is a DataQuantumConsumer that processes a DataQuantum
 * by passing it to each of its Genes in order.
 */
public class Chromosome implements DataQuantumConsumer {
    // An ordered list of Genes
    protected List<Gene> genes = new ArrayList<>();

    @Override
    public void consume(DataQuantum dataQuantum) {
        for (Gene gene : genes) {
            gene.consume(dataQuantum);
        }
    }

    @Override
    public String getId() {
        // Provide a meaningful implementation for the ID
        return "Chromosome-" + hashCode();
    }
}