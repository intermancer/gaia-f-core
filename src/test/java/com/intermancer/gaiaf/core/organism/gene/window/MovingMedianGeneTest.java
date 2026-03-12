package com.intermancer.gaiaf.core.organism.gene.window;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MovingMedianGeneTest {

    private DataQuantum feedValues(MovingMedianGene gene, double... values) {
        DataQuantum dq = null;
        for (double v : values) {
            dq = new DataQuantum();
            dq.addValue(v);
            gene.consume(dq);
        }
        return dq;
    }

    @Test
    public void testMedianOddLength() {
        MovingMedianGene gene = new MovingMedianGene();
        gene.getOperationConstantList().set(0, 3.0);
        DataQuantum dq = feedValues(gene, 3.0, 1.0, 2.0);
        // Sorted: [1, 2, 3] -> median = 2.0
        assertEquals(2.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testMedianEvenLength() {
        MovingMedianGene gene = new MovingMedianGene();
        gene.getOperationConstantList().set(0, 4.0);
        DataQuantum dq = feedValues(gene, 4.0, 1.0, 3.0, 2.0);
        // Sorted: [1, 2, 3, 4] -> median = (2+3)/2 = 2.5
        assertEquals(2.5, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testWarmingCycles() {
        assertEquals(5, new MovingMedianGene().getWarmingCycles());
    }

    @Test
    public void testCopyOf() {
        MovingMedianGene gene = new MovingMedianGene();
        MovingMedianGene copy = gene.copyOf();
        assertNotSame(gene, copy);
        assertEquals(gene, copy);
    }
}
