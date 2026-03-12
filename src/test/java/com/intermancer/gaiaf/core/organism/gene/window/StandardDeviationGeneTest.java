package com.intermancer.gaiaf.core.organism.gene.window;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StandardDeviationGeneTest {

    @Test
    public void testStdDevOfIdenticalValues() {
        StandardDeviationGene gene = new StandardDeviationGene();
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
    public void testStdDevKnownValues() {
        StandardDeviationGene gene = new StandardDeviationGene();
        gene.getOperationConstantList().set(0, 4.0);
        // Values: 2, 4, 4, 4 -> mean=3.5, population stddev ≈ 0.8660
        DataQuantum dq = null;
        for (double v : new double[]{2.0, 4.0, 4.0, 4.0}) {
            dq = new DataQuantum();
            dq.addValue(v);
            gene.consume(dq);
        }
        // population variance = ((2-3.5)^2 + (4-3.5)^2 + (4-3.5)^2 + (4-3.5)^2) / 4
        //                     = (2.25 + 0.25 + 0.25 + 0.25) / 4 = 0.75
        // stddev = sqrt(0.75) ≈ 0.8660
        assertEquals(Math.sqrt(0.75), dq.getValue(-1), 0.0001);
    }

    @Test
    public void testWarmingCycles() {
        assertEquals(5, new StandardDeviationGene().getWarmingCycles());
    }

    @Test
    public void testCopyOf() {
        StandardDeviationGene gene = new StandardDeviationGene();
        assertNotSame(gene, gene.copyOf());
        assertEquals(gene, gene.copyOf());
    }
}
