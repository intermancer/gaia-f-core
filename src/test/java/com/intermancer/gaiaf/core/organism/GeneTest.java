package com.intermancer.gaiaf.core.organism;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GeneTest {

    @Test
    public void testGeneConsume() {
        // Create a Gene
        Gene gene = new Gene() {
            @Override
            public void consume(DataQuantum dataQuantum) {
                // Retrieve the first value, square it, and add it back
                double value = dataQuantum.getValue(0);
                dataQuantum.addValue(value * value);
            }

            @Override
            public String getId() {
                return "Gene-1";
            }
        };

        // Create a DataQuantum and add an initial value
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(3.0);

        // Pass the DataQuantum to the Gene for processing
        gene.consume(dataQuantum);

        // Verify that the Gene squared the value and added it back
        assertEquals(9.0, dataQuantum.getValue(1));
    }

    @Test
    public void testGeneGetId() {
        // Create a Gene
        Gene gene = new Gene() {
            @Override
            public void consume(DataQuantum dataQuantum) {
                // No-op
            }

            @Override
            public String getId() {
                return "Gene-123";
            }
        };

        // Verify the ID is returned correctly
        assertEquals("Gene-123", gene.getId());
    }
}