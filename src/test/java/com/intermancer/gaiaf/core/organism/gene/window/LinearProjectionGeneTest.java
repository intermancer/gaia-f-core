package com.intermancer.gaiaf.core.organism.gene.window;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LinearProjectionGeneTest {

    @Test
    public void testPerfectLinearTrend() {
        LinearProjectionGene gene = new LinearProjectionGene();
        gene.getOperationConstantList().set(0, 3.0);
        // Values 2, 4, 6 are perfectly linear (slope=2, intercept=0)
        DataQuantum dq = null;
        for (double v : new double[]{2.0, 4.0, 6.0}) {
            dq = new DataQuantum();
            dq.addValue(v);
            gene.consume(dq);
        }
        // Next step (position 4): 2*4 + 0 = 8.0
        assertEquals(8.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testSingleValueReturnsItself() {
        LinearProjectionGene gene = new LinearProjectionGene();
        DataQuantum dq = new DataQuantum();
        dq.addValue(5.0);
        gene.consume(dq);
        assertEquals(5.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testFlatLineTrend() {
        LinearProjectionGene gene = new LinearProjectionGene();
        gene.getOperationConstantList().set(0, 3.0);
        DataQuantum dq = null;
        for (double v : new double[]{7.0, 7.0, 7.0}) {
            dq = new DataQuantum();
            dq.addValue(v);
            gene.consume(dq);
        }
        // Flat trend: projection should also be 7.0
        assertEquals(7.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testWarmingCycles() {
        assertEquals(5, new LinearProjectionGene().getWarmingCycles());
    }

    @Test
    public void testCopyOf() {
        LinearProjectionGene gene = new LinearProjectionGene();
        assertNotSame(gene, gene.copyOf());
        assertEquals(gene, gene.copyOf());
    }
}
