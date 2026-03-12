package com.intermancer.gaiaf.core.organism.gene.window;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RangeGeneTest {

    @Test
    public void testRangeOfKnownValues() {
        RangeGene gene = new RangeGene();
        gene.getOperationConstantList().set(0, 4.0);
        DataQuantum dq = null;
        for (double v : new double[]{3.0, 7.0, 1.0, 5.0}) {
            dq = new DataQuantum();
            dq.addValue(v);
            gene.consume(dq);
        }
        // max=7, min=1, range=6
        assertEquals(6.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testRangeOfIdenticalValues() {
        RangeGene gene = new RangeGene();
        gene.getOperationConstantList().set(0, 3.0);
        DataQuantum dq = null;
        for (double v : new double[]{4.0, 4.0, 4.0}) {
            dq = new DataQuantum();
            dq.addValue(v);
            gene.consume(dq);
        }
        assertEquals(0.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testWarmingCycles() {
        assertEquals(5, new RangeGene().getWarmingCycles());
    }

    @Test
    public void testCopyOf() {
        RangeGene gene = new RangeGene();
        assertNotSame(gene, gene.copyOf());
        assertEquals(gene, gene.copyOf());
    }
}
