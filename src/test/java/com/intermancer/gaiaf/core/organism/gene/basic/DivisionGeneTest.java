package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DivisionGeneTest {

    @Test
    public void testDivisionOperation() {
        // Create a DivisionGene
        DivisionGene gene = new DivisionGene();

        // Create a DataQuantum and add a value
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(15.0);

        // Consume the DataQuantum
        gene.consume(dataQuantum);

        // Verify the result
        double expected = 10.0;
        double actual = dataQuantum.getValue(1);
        assertEquals(expected, actual, 0.0001, "The division operation result is incorrect.");
    }

    @Test
    public void testDivisionByZero() {
        // Create a DivisionGene
        DivisionGene gene = new DivisionGene();
        gene.getOperationConstantList().clear();
        gene.getOperationConstantList().add(0.0); // Set the divisor to zero

        // Create a DataQuantum and add a value
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(10.0);

        // Verify that consuming the DataQuantum throws an ArithmeticException
        assertThrows(ArithmeticException.class, () -> gene.consume(dataQuantum), "Division by zero should throw an ArithmeticException.");
    }
}