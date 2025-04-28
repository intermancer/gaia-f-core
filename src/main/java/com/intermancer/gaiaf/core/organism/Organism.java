package com.intermancer.gaiaf.core.organism;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an Organism, which is a DataQuantumConsumer.
 * An Organism contains an ordered list of Chromosomes and processes
 * a DataQuantum by passing it to each Chromosome in order.
 */
public class Organism implements DataQuantumConsumer {
    private final List<Chromosome> chromosomes = new ArrayList<>();
    private final String id;

    /**
     * Constructs an Organism with a unique identifier.
     *
     * @param id The unique identifier for this Organism.
     */
    public Organism(String id) {
        this.id = id;
    }

    /**
     * Adds a Chromosome to the end of the list of chromosomes.
     *
     * @param chromosome The Chromosome to add.
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
    @Override
    public String getId() {
        return id;
    }
}