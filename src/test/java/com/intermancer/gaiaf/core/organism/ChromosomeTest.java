package com.intermancer.gaiaf.core.organism;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChromosomeTest {

    @Test
    public void testChromosomeConsume() {
        // Create a Chromosome
        Chromosome chromosome = new Chromosome();

        // Add a Gene to the Chromosome
        chromosome.genes.add(new Gene() {
            @Override
            public void consume(DataQuantum dataQuantum) {
                // Retrieve the first value, triple it, and add it back
                double value = dataQuantum.getValue(0);
                dataQuantum.addValue(value * 3);
            }

            @Override
            public String getId() {
                return "Gene-1";
            }
        });

        // Create a DataQuantum and add an initial value
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(2.0);

        // Pass the DataQuantum to the Chromosome for processing
        chromosome.consume(dataQuantum);

        // Verify that the Gene tripled the value and added it back
        assertEquals(6.0, dataQuantum.getValue(1));
    }

    @Test
    public void testChromosomeGetId() {
        // Create a Chromosome
        Chromosome chromosome = new Chromosome();

        // Verify the ID is returned correctly
        assertNotNull(chromosome.getId());
        assertTrue(chromosome.getId().startsWith("Chromosome-"));
    }
}