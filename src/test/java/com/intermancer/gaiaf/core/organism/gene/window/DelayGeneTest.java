package com.intermancer.gaiaf.core.organism.gene.window;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DelayGeneTest {

    @Test
    public void testEmitsOldestValue() {
        DelayGene gene = new DelayGene();
        gene.getOperationConstantList().set(0, 3.0);
        DataQuantum dq = null;
        for (double v : new double[]{7.0, 14.0, 21.0}) {
            dq = new DataQuantum();
            dq.addValue(v);
            gene.consume(dq);
        }
        // Oldest value in window of 3 is 7.0
        assertEquals(7.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testDuringWarmupEmitsFirstValue() {
        DelayGene gene = new DelayGene();
        DataQuantum dq = new DataQuantum();
        dq.addValue(42.0);
        gene.consume(dq);
        assertEquals(42.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testWarmingCycles() {
        assertEquals(5, new DelayGene().getWarmingCycles());
    }

    @Test
    public void testCopyOf() {
        DelayGene gene = new DelayGene();
        assertNotSame(gene, gene.copyOf());
        assertEquals(gene, gene.copyOf());
    }
}
