package com.intermancer.gaiaf.core.organism.gene.control;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ThresholdSwitchGeneTest {

    private DataQuantum consume(ThresholdSwitchGene gene, double v1, double v2, double control) {
        DataQuantum dq = new DataQuantum();
        dq.addValue(v1);
        dq.addValue(v2);
        dq.addValue(control);
        gene.consume(dq);
        return dq;
    }

    @Test
    public void testSelectsValue1WhenControlExceedsThreshold() {
        ThresholdSwitchGene gene = new ThresholdSwitchGene(); // threshold = 0.0
        DataQuantum dq = consume(gene, 100.0, 200.0, 1.0); // control > 0
        assertEquals(100.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testSelectsValue2WhenControlBelowThreshold() {
        ThresholdSwitchGene gene = new ThresholdSwitchGene();
        DataQuantum dq = consume(gene, 100.0, 200.0, -1.0); // control < 0
        assertEquals(200.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testSelectsValue2WhenControlEqualsThreshold() {
        ThresholdSwitchGene gene = new ThresholdSwitchGene();
        DataQuantum dq = consume(gene, 100.0, 200.0, 0.0); // control == threshold
        assertEquals(200.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testCustomThreshold() {
        ThresholdSwitchGene gene = new ThresholdSwitchGene();
        gene.getOperationConstantList().set(0, 5.0);
        DataQuantum dq = consume(gene, 100.0, 200.0, 4.9); // below threshold of 5
        assertEquals(200.0, dq.getValue(-1), 0.0001);

        dq = consume(gene, 100.0, 200.0, 5.1); // above threshold of 5
        assertEquals(100.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testCopyOf() {
        ThresholdSwitchGene gene = new ThresholdSwitchGene();
        assertNotSame(gene, gene.copyOf());
        assertEquals(gene, gene.copyOf());
    }
}
