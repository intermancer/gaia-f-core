package com.intermancer.gaiaf.core.organism.gene.control;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ScaledBlendGeneTest {

    private DataQuantum consume(ScaledBlendGene gene, double v1, double v2, double weight) {
        DataQuantum dq = new DataQuantum();
        dq.addValue(v1);
        dq.addValue(v2);
        dq.addValue(weight);
        gene.consume(dq);
        return dq;
    }

    @Test
    public void testWeightZeroReturnsValue2() {
        ScaledBlendGene gene = new ScaledBlendGene();
        DataQuantum dq = consume(gene, 100.0, 200.0, 0.0);
        assertEquals(200.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testWeightOneReturnsValue1() {
        ScaledBlendGene gene = new ScaledBlendGene();
        DataQuantum dq = consume(gene, 100.0, 200.0, 1.0);
        assertEquals(100.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testWeightHalfReturnsAverage() {
        ScaledBlendGene gene = new ScaledBlendGene();
        DataQuantum dq = consume(gene, 100.0, 200.0, 0.5);
        assertEquals(150.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testWeightAboveOneClampedToOne() {
        ScaledBlendGene gene = new ScaledBlendGene();
        DataQuantum dq = consume(gene, 100.0, 200.0, 5.0); // clamped to 1.0
        assertEquals(100.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testWeightBelowZeroClampedToZero() {
        ScaledBlendGene gene = new ScaledBlendGene();
        DataQuantum dq = consume(gene, 100.0, 200.0, -2.0); // clamped to 0.0
        assertEquals(200.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testCopyOf() {
        ScaledBlendGene gene = new ScaledBlendGene();
        assertNotSame(gene, gene.copyOf());
        assertEquals(gene, gene.copyOf());
    }
}
