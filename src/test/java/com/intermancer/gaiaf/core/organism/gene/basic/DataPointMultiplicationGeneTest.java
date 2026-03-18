package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataPointMultiplicationGeneTest {

    @Test
    public void testMultipliesTwoDataPoints() {
        DataPointMultiplicationGene gene = new DataPointMultiplicationGene();
        DataQuantum dq = new DataQuantum();
        dq.addValue(4.0);
        dq.addValue(5.0);

        gene.consume(dq);

        assertEquals(20.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testMultiplyByZero() {
        DataPointMultiplicationGene gene = new DataPointMultiplicationGene();
        DataQuantum dq = new DataQuantum();
        dq.addValue(100.0);
        dq.addValue(0.0);

        gene.consume(dq);

        assertEquals(0.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testCopyOf() {
        DataPointMultiplicationGene gene = new DataPointMultiplicationGene();
        DataPointMultiplicationGene copy = gene.copyOf();
        assertNotSame(gene, copy);
        assertEquals(gene, copy);
    }
}
