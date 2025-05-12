package com.intermancer.gaiaf.core.organism;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an Organism, which is a DataQuantumConsumer.
 * An Organism contains an ordered list of Chromosomes and processes
 * a DataQuantum by passing it to each Chromosome in order.
 */
public class Organism implements DataQuantumConsumer {
    private List<Chromosome> chromosomes;
    private String id;

    /**
     * Default constructor for Jackson deserialization.
     */
    public Organism() {
        this.chromosomes = new ArrayList<>();
    }

    /**
     * Constructs an Organism with a unique identifier.
     *
     * @param id The unique identifier for this Organism.
     */
    @JsonCreator
    public Organism(@JsonProperty("id") String id) {
        this();  // Call the no-argument constructor
        this.id = id;
    }

    /**
     * Adds a Chromosome to the end of the list of chromosomes.
     *
     * @param chromosome The Chromosome to add.
     * @throws IllegalArgumentException if the chromosome is null.
     */
    public void addChromosome(Chromosome chromosome) {
        if (chromosome == null) {
            throw new IllegalArgumentException("Chromosome cannot be null");
        }
        chromosomes.add(chromosome);
    }

    /**
     * Returns the list of chromosomes.
     *
     * @return The list of Chromosomes.
     */
    public List<Chromosome> getChromosomes() {
        return chromosomes;
    }

    /**
     * Sets the list of chromosomes.
     *
     * @param chromosomes The list of chromosomes to set
     */
    public void setChromosomes(List<Chromosome> chromosomes) {
        this.chromosomes.clear();
        if (chromosomes != null) {
            this.chromosomes.addAll(chromosomes);
        }
    }

    /**
     * Sets the ID for this Organism.
     *
     * @param id The ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Processes the given DataQuantum by passing it to each Chromosome
     * in the list, in order.
     *
     * @param dataQuantum The DataQuantum to process.
     */
    @Override
    public void consume(DataQuantum dataQuantum) {
        for (Chromosome chromosome : chromosomes) {
            chromosome.consume(dataQuantum);
        }
    }

    /**
     * Returns the unique identifier for this Organism.
     *
     * @return The unique ID as a String.
     */
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Organism other = (Organism) obj;
        
        // Compare chromosomes list
        if (chromosomes == null) {
            if (other.chromosomes != null) return false;
        } else if (!chromosomes.equals(other.chromosomes)) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((chromosomes == null) ? 0 : chromosomes.hashCode());
        return result;
    }
}