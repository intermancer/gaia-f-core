package com.intermancer.gaiaf.core.organism;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GeneTest {
    @Test
    public void testGeneConsume() {
        // Implement a concrete Gene for testing
        Gene gene = new Gene() {
            @Override
            public void consume(DataQuantum dataQuantum) {
                double value = dataQuantum.getValue(0);
                dataQuantum.addValue(value * 2);
            }
        };

        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(1.0);
        gene.consume(dataQuantum);
        assertEquals(2.0, dataQuantum.getValue(1));
    }
}