package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataPointSubtractionGeneTest {

    @Test
    public void testSubtractsTwoDataPoints() {
        DataPointSubtractionGene gene = new DataPointSubtractionGene();
        DataQuantum dq = new DataQuantum();
        dq.addValue(10.0);
        dq.addValue(3.0);

        gene.consume(dq);

        assertEquals(7.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testResultIsNegativeWhenSecondIsLarger() {
        DataPointSubtractionGene gene = new DataPointSubtractionGene();
        DataQuantum dq = new DataQuantum();
        dq.addValue(3.0);
        dq.addValue(10.0);

        gene.consume(dq);

        assertEquals(-7.0, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testCopyOf() {
        DataPointSubtractionGene gene = new DataPointSubtractionGene();
        DataPointSubtractionGene copy = gene.copyOf();
        assertNotSame(gene, copy);
        assertEquals(gene, copy);
    }
}
