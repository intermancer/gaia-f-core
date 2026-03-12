package com.intermancer.gaiaf.core.organism.gene.window;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MomentumGeneTest {

    @Test
    public void testMomentumIsNewestMinusOldest() {
        MomentumGene gene = new MomentumGene();
        gene.getOperationConstantList().set(0, 3.0);
        DataQuantum dq = null;
        for (double v : new double[]{10.0, 15.0, 25.0}) {
            dq = new DataQuantum();
            dq.addValue(v);
            gene.consume(dq);
        }
        // newest(25) - oldest(10) = 15.0
        assertEquals(15.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testNegativeMomentum() {
        MomentumGene gene = new MomentumGene();
        gene.getOperationConstantList().set(0, 3.0);
        DataQuantum dq = null;
        for (double v : new double[]{30.0, 20.0, 10.0}) {
            dq = new DataQuantum();
            dq.addValue(v);
            gene.consume(dq);
        }
        assertEquals(-20.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testWarmingCycles() {
        assertEquals(5, new MomentumGene().getWarmingCycles());
    }

    @Test
    public void testCopyOf() {
        MomentumGene gene = new MomentumGene();
        assertNotSame(gene, gene.copyOf());
        assertEquals(gene, gene.copyOf());
    }
}
