package com.intermancer.gaiaf.core.organism;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrganismTest {

    @Test
    public void testOrganismConsume() {
        // Create an Organism with a unique ID
        Organism organism = new Organism("Organism-1");

        // Add a Chromosome to the Organism
        Chromosome chromosome = new Chromosome();
        organism.addChromosome(chromosome);

        // Add a Gene to the Chromosome
        chromosome.genes.add(new Gene() {
            @Override
            public void consume(DataQuantum dataQuantum) {
                // Retrieve the first value, double it, and add it back
                double value = dataQuantum.getValue(0);
                dataQuantum.addValue(value * 2);
            }

            @Override
            public String getId() {
                return "Gene-1";
            }
        });

        // Create a DataQuantum and add an initial value
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(1.0);

        // Pass the DataQuantum to the Organism for processing
        organism.consume(dataQuantum);

        // Verify that the Gene doubled the value and added it back
        assertEquals(2.0, dataQuantum.getValue(1));
    }

    @Test
    public void testOrganismGetId() {
        // Create an Organism with a unique ID
        Organism organism = new Organism("Organism-123");

        // Verify the ID is returned correctly
        assertEquals("Organism-123", organism.getId());
    }

    @Test
    public void testOrganismWithMultipleChromosomesAndGenes() {
        // Create an Organism with a unique ID
        Organism organism = new Organism("Organism-2");

        // Add multiple Chromosomes to the Organism
        Chromosome chromosome1 = new Chromosome();
        Chromosome chromosome2 = new Chromosome();
        organism.addChromosome(chromosome1);
        organism.addChromosome(chromosome2);

        // Add Genes to the Chromosomes
        chromosome1.genes.add(new Gene() {
            @Override
            public void consume(DataQuantum dataQuantum) {
                // Retrieve the first value, triple it, and add it back
                double value = dataQuantum.getValue(0);
                dataQuantum.addValue(value * 3);
            }

            @Override
            public String getId() {
                return "Gene-2";
            }
        });

        chromosome2.genes.add(new Gene() {
            @Override
            public void consume(DataQuantum dataQuantum) {
                // Retrieve the first value, quadruple it, and add it back
                double value = dataQuantum.getValue(0);
                dataQuantum.addValue(value * 4);
            }

            @Override
            public String getId() {
                return "Gene-3";
            }
        });

        // Create a DataQuantum and add an initial value
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(1.0);

        // Pass the DataQuantum to the Organism for processing
        organism.consume(dataQuantum);

        // Verify that the Genes processed the value correctly
        assertEquals(3.0, dataQuantum.getValue(1));
        assertEquals(4.0, dataQuantum.getValue(2));
    }
}