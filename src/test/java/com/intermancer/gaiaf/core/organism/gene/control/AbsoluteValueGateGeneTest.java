package com.intermancer.gaiaf.core.organism.gene.control;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AbsoluteValueGateGeneTest {

    private DataQuantum consume(AbsoluteValueGateGene gene, double value, double gate) {
        DataQuantum dq = new DataQuantum();
        dq.addValue(value);
        dq.addValue(gate);
        gene.consume(dq);
        return dq;
    }

    @Test
    public void testPassesThroughWhenGateExceedsThreshold() {
        AbsoluteValueGateGene gene = new AbsoluteValueGateGene(); // threshold = 1.0
        DataQuantum dq = consume(gene, 42.0, 2.0); // |2| > 1
        assertEquals(42.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testSuppressesWhenGateBelowThreshold() {
        AbsoluteValueGateGene gene = new AbsoluteValueGateGene();
        DataQuantum dq = consume(gene, 42.0, 0.5); // |0.5| < 1
        assertEquals(0.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testNegativeGateAbsoluteValue() {
        AbsoluteValueGateGene gene = new AbsoluteValueGateGene();
        DataQuantum dq = consume(gene, 99.0, -5.0); // |-5| > 1
        assertEquals(99.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testWarmingCyclesIsZero() {
        assertEquals(0, new AbsoluteValueGateGene().getWarmingCycles());
    }

    @Test
    public void testCopyOf() {
        AbsoluteValueGateGene gene = new AbsoluteValueGateGene();
        assertNotSame(gene, gene.copyOf());
        assertEquals(gene, gene.copyOf());
    }
}
