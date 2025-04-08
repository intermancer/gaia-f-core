package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdditionGeneTest {

    @Test
    public void testAdditionOperation() {
        // Create an AdditionGene
        AdditionGene gene = new AdditionGene();

        // Create a DataQuantum and add a DataPoint
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(12.5);

        // Consume the DataQuantum
        gene.consume(dataQuantum);

        // Verify the result
        double expected = 14.0;
        double actual = dataQuantum.getValue(1);
        assertEquals(expected, actual, 0.0001, "The addition operation result is incorrect.");
    }

    @Test
    public void testAdditionWithNegativeConstant() {
        // Create an AdditionGene
        AdditionGene gene = new AdditionGene();
        gene.getOperationConstantList().clear();
        gene.getOperationConstantList().add(-3.0);

        // Create a DataQuantum and add a DataPoint
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(8.0);

        // Consume the DataQuantum
        gene.consume(dataQuantum);

        // Verify the result
        double expected = 5.0;
        double actual = dataQuantum.getValue(1);
        assertEquals(expected, actual, 0.0001, "The addition with a negative constant result is incorrect.");
    }

    @Test
    public void testAdditionWithZeroConstant() {
        // Create an AdditionGene
        AdditionGene gene = new AdditionGene();
        gene.getOperationConstantList().clear();
        gene.getOperationConstantList().add(0.0);

        // Create a DataQuantum and add a DataPoint
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(10.0);

        // Consume the DataQuantum
        gene.consume(dataQuantum);

        // Verify the result
        double expected = 10.0;
        double actual = dataQuantum.getValue(1);
        assertEquals(expected, actual, 0.0001, "The addition with zero constant result is incorrect.");
    }
}