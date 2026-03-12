package com.intermancer.gaiaf.core.organism.gene.control;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SelectMaxGeneTest {

    private DataQuantum consume(SelectMaxGene gene, double v1, double v2) {
        DataQuantum dq = new DataQuantum();
        dq.addValue(v1);
        dq.addValue(v2);
        gene.consume(dq);
        return dq;
    }

    @Test
    public void testSelectsLargerValue() {
        SelectMaxGene gene = new SelectMaxGene();
        assertEquals(8.0, consume(gene, 3.0, 8.0).getValue(-1), 0.0001);
    }

    @Test
    public void testSelectsFirstWhenEqual() {
        SelectMaxGene gene = new SelectMaxGene();
        assertEquals(5.0, consume(gene, 5.0, 5.0).getValue(-1), 0.0001);
    }

    @Test
    public void testNegativeValues() {
        SelectMaxGene gene = new SelectMaxGene();
        assertEquals(-1.0, consume(gene, -1.0, -9.0).getValue(-1), 0.0001);
    }

    @Test
    public void testWarmingCyclesIsZero() {
        assertEquals(0, new SelectMaxGene().getWarmingCycles());
    }

    @Test
    public void testCopyOf() {
        SelectMaxGene gene = new SelectMaxGene();
        assertNotSame(gene, gene.copyOf());
        assertEquals(gene, gene.copyOf());
    }
}
