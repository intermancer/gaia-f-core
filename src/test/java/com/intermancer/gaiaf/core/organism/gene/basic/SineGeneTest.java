package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SineGeneTest {

    @Test
    public void testSineOperation() {
        // Create a SineGene
        SineGene gene = new SineGene();

        // Create a DataQuantum and add a value
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(Math.PI / 2);

        // Consume the DataQuantum
        gene.consume(dataQuantum);

        // Verify the result
        double expected = 1.0;
        double actual = dataQuantum.getValue(1);
        assertEquals(expected, actual, 0.0001, "The sine operation result is incorrect.");
    }
}