package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataPointAdditionGeneTest {

    @Test
    public void testAddsTwoDataPoints() {
        DataPointAdditionGene gene = new DataPointAdditionGene();
        DataQuantum dq = new DataQuantum();
        dq.addValue(3.0);
        dq.addValue(7.0);

        gene.consume(dq);

        assertEquals(10.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testDefaultTargetIndices() {
        DataPointAdditionGene gene = new DataPointAdditionGene();
        assertEquals(2, gene.getTargetIndexList().size());
        assertEquals(-2, gene.getTargetIndexList().get(0));
        assertEquals(-1, gene.getTargetIndexList().get(1));
    }

    @Test
    public void testNoOperationConstants() {
        DataPointAdditionGene gene = new DataPointAdditionGene();
        assertTrue(gene.getOperationConstantList().isEmpty());
    }

    @Test
    public void testNegativeValues() {
        DataPointAdditionGene gene = new DataPointAdditionGene();
        DataQuantum dq = new DataQuantum();
        dq.addValue(-4.0);
        dq.addValue(-6.0);

        gene.consume(dq);

        assertEquals(-10.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testCopyOf() {
        DataPointAdditionGene gene = new DataPointAdditionGene();
        DataPointAdditionGene copy = gene.copyOf();
        assertNotSame(gene, copy);
        assertEquals(gene, copy);
    }

    @Test
    public void testWarmingCyclesIsZero() {
        assertEquals(0, new DataPointAdditionGene().getWarmingCycles());
    }
}
