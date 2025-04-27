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
    private List<Gene> genes = new ArrayList<>();
    private String id;

    /**
     * Gets the list of Genes in this Chromosome.
     *
     * @return The list of Genes
     */
    public List<Gene> getGenes() {
        return genes;
    }

    /**
     * Sets the list of Genes for this Chromosome.
     *
     * @param genes The list of Genes to set
     */
    public void setGenes(List<Gene> genes) {
        this.genes = genes;
    }

    @Override
    public void consume(DataQuantum dataQuantum) {
        for (Gene gene : genes) {
            gene.consume(dataQuantum);
        }
    }

    @Override
    public String getId() {
        // If id is not set, provide a meaningful implementation
        return id != null ? id : "Chromosome-" + hashCode();
    }

    /**
     * Sets the ID for this Chromosome.
     *
     * @param id The ID to set
     */
    public void setId(String id) {
        this.id = id;
    }
}