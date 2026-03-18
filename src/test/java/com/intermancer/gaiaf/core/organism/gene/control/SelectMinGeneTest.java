package com.intermancer.gaiaf.core.organism.gene.control;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SelectMinGeneTest {

    private DataQuantum consume(SelectMinGene gene, double v1, double v2) {
        DataQuantum dq = new DataQuantum();
        dq.addValue(v1);
        dq.addValue(v2);
        gene.consume(dq);
        return dq;
    }

    @Test
    public void testSelectsSmallerValue() {
        SelectMinGene gene = new SelectMinGene();
        assertEquals(3.0, consume(gene, 3.0, 8.0).getValue(-1), 0.0001);
    }

    @Test
    public void testSelectsFirstWhenEqual() {
        SelectMinGene gene = new SelectMinGene();
        assertEquals(5.0, consume(gene, 5.0, 5.0).getValue(-1), 0.0001);
    }

    @Test
    public void testNegativeValues() {
        SelectMinGene gene = new SelectMinGene();
        assertEquals(-9.0, consume(gene, -1.0, -9.0).getValue(-1), 0.0001);
    }

    @Test
    public void testWarmingCyclesIsZero() {
        assertEquals(0, new SelectMinGene().getWarmingCycles());
    }

    @Test
    public void testCopyOf() {
        SelectMinGene gene = new SelectMinGene();
        assertNotSame(gene, gene.copyOf());
        assertEquals(gene, gene.copyOf());
    }
}
