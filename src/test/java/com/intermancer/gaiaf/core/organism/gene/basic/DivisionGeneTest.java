package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DivisionGeneTest {

    @Test
    public void testDivisionOperation() {
        DivisionGene gene = new DivisionGene();
        gene.setIndex(0);
        gene.setAppliedConstant(2.0);

        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(10.0);

        gene.consume(dataQuantum);

        assertEquals(5.0, dataQuantum.getValue(1), 0.0001);
    }

    @Test
    public void testDivisionByZero() {
        DivisionGene gene = new DivisionGene();
        gene.setIndex(0);
        gene.setAppliedConstant(0.0);

        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(10.0);

        assertThrows(ArithmeticException.class, () -> gene.consume(dataQuantum));
    }
}