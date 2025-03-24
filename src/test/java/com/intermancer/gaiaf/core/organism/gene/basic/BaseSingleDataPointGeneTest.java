package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BaseSingleDataPointGeneTest {

    @Test
    public void testAdditionGene() {
        // Create an AdditionGene
        AdditionGene gene = new AdditionGene();
        gene.setIndex(0);
        gene.setAppliedConstant(5.0);

        // Create a DataQuantum and add a DataPoint
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(10.0);

        // Consume the DataQuantum
        gene.consume(dataQuantum);

        // Verify the result
        assertEquals(15.0, dataQuantum.getValue(1));
    }
}