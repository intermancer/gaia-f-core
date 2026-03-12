package com.intermancer.gaiaf.core.organism.gene.control;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SignGeneTest {

    private DataQuantum consume(SignGene gene, double value) {
        DataQuantum dq = new DataQuantum();
        dq.addValue(value);
        gene.consume(dq);
        return dq;
    }

    @Test
    public void testPositiveValueReturnsMagnitude() {
        SignGene gene = new SignGene(); // magnitude = 1.0
        assertEquals(1.0, consume(gene, 5.0).getValue(-1), 0.0001);
    }

    @Test
    public void testNegativeValueReturnsNegativeMagnitude() {
        SignGene gene = new SignGene();
        assertEquals(-1.0, consume(gene, -3.0).getValue(-1), 0.0001);
    }

    @Test
    public void testZeroValueReturnsZero() {
        SignGene gene = new SignGene();
        assertEquals(0.0, consume(gene, 0.0).getValue(-1), 0.0001);
    }

    @Test
    public void testCustomMagnitude() {
        SignGene gene = new SignGene();
        gene.getOperationConstantList().set(0, 5.0);
        assertEquals(5.0, consume(gene, 1.0).getValue(-1), 0.0001);
        assertEquals(-5.0, consume(gene, -1.0).getValue(-1), 0.0001);
    }

    @Test
    public void testWarmingCyclesIsZero() {
        assertEquals(0, new SignGene().getWarmingCycles());
    }

    @Test
    public void testCopyOf() {
        SignGene gene = new SignGene();
        assertNotSame(gene, gene.copyOf());
        assertEquals(gene, gene.copyOf());
    }
}
