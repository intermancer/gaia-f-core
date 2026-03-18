package com.intermancer.gaiaf.core.organism.gene.window;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MovingAverageGeneTest {

    @Test
    public void testAverageOfFullWindow() {
        MovingAverageGene gene = new MovingAverageGene();
        // Window size default is 5
        double[] values = {2.0, 4.0, 6.0, 8.0, 10.0};
        DataQuantum dq = null;
        for (double v : values) {
            dq = new DataQuantum();
            dq.addValue(v);
            gene.consume(dq);
        }
        // Average of [2,4,6,8,10] = 6.0
        assertEquals(6.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testWarmupPeriodUsesAvailableValues() {
        MovingAverageGene gene = new MovingAverageGene();
        DataQuantum dq = new DataQuantum();
        dq.addValue(4.0);
        gene.consume(dq);
        // Only one value buffered; average = 4.0
        assertEquals(4.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testWindowSizeReturnsCorrectWarmingCycles() {
        MovingAverageGene gene = new MovingAverageGene();
        assertEquals(5, gene.getWarmingCycles());
    }

    @Test
    public void testCopyOf() {
        MovingAverageGene gene = new MovingAverageGene();
        MovingAverageGene copy = gene.copyOf();
        assertNotSame(gene, copy);
        assertEquals(gene, copy);
    }

    @Test
    public void testCustomWindowSize() {
        MovingAverageGene gene = new MovingAverageGene();
        gene.getOperationConstantList().set(0, 3.0);
        assertEquals(3, gene.getWarmingCycles());

        // Feed 4 values; window of 3 should average the last 3
        double[] input = {10.0, 20.0, 30.0, 40.0};
        DataQuantum dq = null;
        for (double v : input) {
            dq = new DataQuantum();
            dq.addValue(v);
            gene.consume(dq);
        }
        // Last 3 values: [20, 30, 40] -> average = 30.0
        assertEquals(30.0, dq.getValue(-1), 0.0001);
    }
}
