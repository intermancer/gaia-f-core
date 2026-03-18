package com.intermancer.gaiaf.core.organism.gene.window;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExponentialMovingAverageGeneTest {

    @Test
    public void testFirstValueInitializesEma() {
        ExponentialMovingAverageGene gene = new ExponentialMovingAverageGene();
        DataQuantum dq = new DataQuantum();
        dq.addValue(10.0);
        gene.consume(dq);
        // First call: EMA initialized to first value
        assertEquals(10.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testEmaConvergesWithAlpha() {
        ExponentialMovingAverageGene gene = new ExponentialMovingAverageGene();
        // Set alpha to 0.5 for easy manual calculation
        gene.getOperationConstantList().set(1, 0.5);

        DataQuantum dq = new DataQuantum();
        dq.addValue(10.0);
        gene.consume(dq); // EMA = 10

        dq = new DataQuantum();
        dq.addValue(20.0);
        gene.consume(dq); // EMA = 0.5*20 + 0.5*10 = 15.0

        assertEquals(15.0, dq.getValue(-1), 0.0001);

        dq = new DataQuantum();
        dq.addValue(30.0);
        gene.consume(dq); // EMA = 0.5*30 + 0.5*15 = 22.5

        assertEquals(22.5, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testDefaultAlphaIsPoint2() {
        ExponentialMovingAverageGene gene = new ExponentialMovingAverageGene();
        assertEquals(0.2, gene.getOperationConstantList().get(1), 0.0001);
    }

    @Test
    public void testWarmingCycles() {
        assertEquals(5, new ExponentialMovingAverageGene().getWarmingCycles());
    }

    @Test
    public void testCopyOfPreservesState() {
        ExponentialMovingAverageGene gene = new ExponentialMovingAverageGene();
        DataQuantum dq = new DataQuantum();
        dq.addValue(10.0);
        gene.consume(dq);

        ExponentialMovingAverageGene copy = gene.copyOf();
        assertNotSame(gene, copy);

        // Both should produce the same next EMA
        DataQuantum dqOriginal = new DataQuantum();
        dqOriginal.addValue(20.0);
        gene.consume(dqOriginal);

        DataQuantum dqCopy = new DataQuantum();
        dqCopy.addValue(20.0);
        copy.consume(dqCopy);

        assertEquals(dqOriginal.getValue(-1), dqCopy.getValue(-1), 0.0001);
    }
}
