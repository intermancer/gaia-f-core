package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SineGeneTest {

    @Test
    public void testSineOperation() {
        SineGene gene = new SineGene();
        gene.setIndex(0);

        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(Math.PI / 2);

        gene.consume(dataQuantum);

        assertEquals(1.0, dataQuantum.getValue(1), 0.0001);
    }
}