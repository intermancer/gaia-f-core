package com.intermancer.gaiaf.core.organism.gene.control;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClampGeneTest {

    private DataQuantum consume(ClampGene gene, double value, double floor, double ceiling) {
        DataQuantum dq = new DataQuantum();
        dq.addValue(value);
        dq.addValue(floor);
        dq.addValue(ceiling);
        gene.consume(dq);
        return dq;
    }

    @Test
    public void testValueWithinBoundsPassesThrough() {
        ClampGene gene = new ClampGene();
        DataQuantum dq = consume(gene, 5.0, 1.0, 10.0);
        assertEquals(5.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testValueBelowFloorClampsToFloor() {
        ClampGene gene = new ClampGene();
        DataQuantum dq = consume(gene, -5.0, 0.0, 10.0);
        assertEquals(0.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testValueAboveCeilingClampsToceiling() {
        ClampGene gene = new ClampGene();
        DataQuantum dq = consume(gene, 15.0, 0.0, 10.0);
        assertEquals(10.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testDefaultTargetIndices() {
        ClampGene gene = new ClampGene();
        assertEquals(3, gene.getTargetIndexList().size());
        assertEquals(-3, gene.getTargetIndexList().get(0));
        assertEquals(-2, gene.getTargetIndexList().get(1));
        assertEquals(-1, gene.getTargetIndexList().get(2));
    }

    @Test
    public void testWarmingCyclesIsZero() {
        assertEquals(0, new ClampGene().getWarmingCycles());
    }

    @Test
    public void testCopyOf() {
        ClampGene gene = new ClampGene();
        assertNotSame(gene, gene.copyOf());
        assertEquals(gene, gene.copyOf());
    }
}
