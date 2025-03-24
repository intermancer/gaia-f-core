package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MultiplicationGeneTest {

    @Test
    public void testMultiplicationOperation() {
        MultiplicationGene gene = new MultiplicationGene();
        gene.setIndex(0);
        gene.setAppliedConstant(2.0);

        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(5.0);

        gene.consume(dataQuantum);

        assertEquals(10.0, dataQuantum.getValue(1), 0.0001);
    }
}