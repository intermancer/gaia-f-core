package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubtractionGeneTest {

    @Test
    public void testSubtractionOperation() {
        SubtractionGene gene = new SubtractionGene();
        gene.setIndex(0);
        gene.setAppliedConstant(3.0);

        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(10.0);

        gene.consume(dataQuantum);

        assertEquals(7.0, dataQuantum.getValue(1), 0.0001);
    }
}