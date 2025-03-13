package com.intermancer.gaiaf.core.organism;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChromosomeTest {
    @Test
    public void testChromosomeConsume() {
        // Implement a concrete Chromosome for testing
        Chromosome chromosome = new Chromosome() {
            {
                genes.add(new Gene() {
                    @Override
                    public void consume(DataQuantum dataQuantum) {
                        double value = dataQuantum.getValue(0);
                        dataQuantum.addValue(value * 2);
                    }
                });
            }
        };

        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(1.0);
        chromosome.consume(dataQuantum);
        assertEquals(2.0, dataQuantum.getValue(1));
    }
}