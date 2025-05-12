package com.intermancer.gaiaf.core.organism;

import java.util.ArrayList;
import java.util.List;

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
    
    /**
     * Creates a clone of this Chromosome.
     * The clone contains deep copies of all genes in this chromosome.
     *
     * @return A new Chromosome that is a deep copy of this one
     */
    public Chromosome copyOf() {
        Chromosome clone = new Chromosome();
        
        // Clone each gene and add it to the new chromosome
        for (Gene gene : this.genes) {
            clone.getGenes().add(gene.copyOf());
        }
        
        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Chromosome other = (Chromosome) obj;
        
        // Compare genes list
        if (genes == null) {
            if (other.genes != null) return false;
        } else if (!genes.equals(other.genes)) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((genes == null) ? 0 : genes.hashCode());
        return result;
    }
}