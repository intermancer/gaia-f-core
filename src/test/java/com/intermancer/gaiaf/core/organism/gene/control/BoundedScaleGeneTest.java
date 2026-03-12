package com.intermancer.gaiaf.core.organism.gene.control;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoundedScaleGeneTest {

    private DataQuantum consume(BoundedScaleGene gene, double value, double scale) {
        DataQuantum dq = new DataQuantum();
        dq.addValue(value);
        dq.addValue(scale);
        gene.consume(dq);
        return dq;
    }

    @Test
    public void testScaleWithinBoundsAppliedDirectly() {
        BoundedScaleGene gene = new BoundedScaleGene(); // bound = 2.0
        DataQuantum dq = consume(gene, 10.0, 1.5); // 1.5 within [-2, 2]
        assertEquals(15.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testScaleAboveBoundClamped() {
        BoundedScaleGene gene = new BoundedScaleGene();
        DataQuantum dq = consume(gene, 10.0, 5.0); // clamped to 2.0
        assertEquals(20.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testScaleBelowNegativeBoundClamped() {
        BoundedScaleGene gene = new BoundedScaleGene();
        DataQuantum dq = consume(gene, 10.0, -5.0); // clamped to -2.0
        assertEquals(-20.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testWarmingCyclesIsZero() {
        assertEquals(0, new BoundedScaleGene().getWarmingCycles());
    }

    @Test
    public void testCopyOf() {
        BoundedScaleGene gene = new BoundedScaleGene();
        assertNotSame(gene, gene.copyOf());
        assertEquals(gene, gene.copyOf());
    }
}
