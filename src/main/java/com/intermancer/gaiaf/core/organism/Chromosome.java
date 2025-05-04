package com.intermancer.gaiaf.core.organism;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Represents a Chromosome, which is an ordered list of Genes.
 * A Chromosome is a DataQuantumConsumer that processes a DataQuantum
 * by passing it to each of its Genes in order.
 */
public class Chromosome implements DataQuantumConsumer {
    private List<Gene> genes;
    private String id;

    /**
     * Default constructor for Jackson deserialization.
     */
    public Chromosome() {
        this.genes = new ArrayList<>();
    }

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

    /**
     * Gets the ID of this Chromosome.
     *
     * @return The ID as a String
     */
    @Override
    public String getId() {
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

    /**
     * Processes the given DataQuantum by passing it to each Gene
     * in the list, in order.
     *
     * @param dataQuantum The DataQuantum to process.
     */
    @Override
    public void consume(DataQuantum dataQuantum) {
        for (Gene gene : genes) {
            gene.consume(dataQuantum);
        }
    }
}