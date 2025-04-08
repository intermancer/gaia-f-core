package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MultiplicationGeneTest {

    @Test
    public void testMultiplicationOperation() {
        // Create a MultiplicationGene
        MultiplicationGene gene = new MultiplicationGene();

        // Create a DataQuantum and add a value
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(5.0);

        // Consume the DataQuantum
        gene.consume(dataQuantum);

        // Verify the result
        double expected = 7.5;
        double actual = dataQuantum.getValue(1);
        assertEquals(expected, actual, 0.0001, "The multiplication operation result is incorrect.");
    }
}