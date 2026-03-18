package com.intermancer.gaiaf.core.organism.gene.control;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MagnitudeGateGeneTest {

    private DataQuantum consume(MagnitudeGateGene gene, double value, double control) {
        DataQuantum dq = new DataQuantum();
        dq.addValue(value);
        dq.addValue(control);
        gene.consume(dq);
        return dq;
    }

    @Test
    public void testPositiveControlKeepsSign() {
        MagnitudeGateGene gene = new MagnitudeGateGene();
        assertEquals(10.0, consume(gene, 10.0, 3.0).getValue(-1), 0.0001);
    }

    @Test
    public void testNegativeControlInvertsSign() {
        MagnitudeGateGene gene = new MagnitudeGateGene();
        assertEquals(-10.0, consume(gene, 10.0, -3.0).getValue(-1), 0.0001);
    }

    @Test
    public void testZeroControlGivesZero() {
        MagnitudeGateGene gene = new MagnitudeGateGene();
        assertEquals(0.0, consume(gene, 10.0, 0.0).getValue(-1), 0.0001);
    }

    @Test
    public void testWarmingCyclesIsZero() {
        assertEquals(0, new MagnitudeGateGene().getWarmingCycles());
    }

    @Test
    public void testCopyOf() {
        MagnitudeGateGene gene = new MagnitudeGateGene();
        assertNotSame(gene, gene.copyOf());
        assertEquals(gene, gene.copyOf());
    }
}
