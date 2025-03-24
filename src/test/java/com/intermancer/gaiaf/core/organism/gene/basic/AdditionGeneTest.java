package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdditionGeneTest {

    @Test
    public void testAdditionOperation() {
        // Create an AdditionGene
        AdditionGene gene = new AdditionGene();
        gene.setIndex(0);
        gene.setAppliedConstant(7.5);

        // Create a DataQuantum and add a DataPoint
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(12.5);

        // Consume the DataQuantum
        gene.consume(dataQuantum);

        // Verify the result
        assertEquals(20.0, dataQuantum.getValue(1), 0.0001);
    }

    @Test
    public void testAdditionWithNegativeConstant() {
        // Create an AdditionGene
        AdditionGene gene = new AdditionGene();
        gene.setIndex(0);
        gene.setAppliedConstant(-3.0);

        // Create a DataQuantum and add a DataPoint
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(8.0);

        // Consume the DataQuantum
        gene.consume(dataQuantum);

        // Verify the result
        assertEquals(5.0, dataQuantum.getValue(1), 0.0001);
    }

    @Test
    public void testAdditionWithZeroConstant() {
        // Create an AdditionGene
        AdditionGene gene = new AdditionGene();
        gene.setIndex(0);
        gene.setAppliedConstant(0.0);

        // Create a DataQuantum and add a DataPoint
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(10.0);

        // Consume the DataQuantum
        gene.consume(dataQuantum);

        // Verify the result
        assertEquals(10.0, dataQuantum.getValue(1), 0.0001);
    }
}