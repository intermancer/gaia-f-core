package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataPointDivisionGeneTest {

    @Test
    public void testDividesTwoDataPoints() {
        DataPointDivisionGene gene = new DataPointDivisionGene();
        DataQuantum dq = new DataQuantum();
        dq.addValue(10.0);
        dq.addValue(4.0);

        gene.consume(dq);

        assertEquals(2.5, dq.getValue(-1), 0.0001);
    }

    @Test
    public void testDivisionByZeroOutputsZero() {
        DataPointDivisionGene gene = new DataPointDivisionGene();
        DataQuantum dq = new DataQuantum();
        dq.addValue(10.0);
        dq.addValue(0.0);

        gene.consume(dq);

        assertEquals(0.0, dq.getValue(-1), 0.0001, "Division by zero should output 0.0.");
    }

    @Test
    public void testCopyOf() {
        DataPointDivisionGene gene = new DataPointDivisionGene();
        DataPointDivisionGene copy = gene.copyOf();
        assertNotSame(gene, copy);
        assertEquals(gene, copy);
    }
}
