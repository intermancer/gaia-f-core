package com.intermancer.gaiaf.core.organism.gene.window;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ZScoreGeneTest {

    @Test
    public void testZScoreOfMeanIsZero() {
        ZScoreGene gene = new ZScoreGene();
        gene.getOperationConstantList().set(0, 3.0);
        DataQuantum dq = null;
        // Feed values that make the last value exactly the mean
        for (double v : new double[]{1.0, 2.0, 3.0}) {
            dq = new DataQuantum();
            dq.addValue(v);
            gene.consume(dq);
        }
        // mean=2, last=3, stddev=sqrt(2/3) ≈ 0.8165
        // z = (3-2)/0.8165 ≈ 1.2247
        assertEquals((3.0 - 2.0) / Math.sqrt(2.0 / 3.0), dq.getValue(-1), 0.0001);
    }

    @Test
    public void testZScoreReturnsZeroWhenStdDevIsZero() {
        ZScoreGene gene = new ZScoreGene();
        gene.getOperationConstantList().set(0, 3.0);
        DataQuantum dq = null;
        for (double v : new double[]{5.0, 5.0, 5.0}) {
            dq = new DataQuantum();
            dq.addValue(v);
            gene.consume(dq);
        }
        assertEquals(0.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testWarmingCycles() {
        assertEquals(5, new ZScoreGene().getWarmingCycles());
    }

    @Test
    public void testCopyOf() {
        ZScoreGene gene = new ZScoreGene();
        assertNotSame(gene, gene.copyOf());
        assertEquals(gene, gene.copyOf());
    }
}
