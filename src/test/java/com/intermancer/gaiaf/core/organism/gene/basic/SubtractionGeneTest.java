package com.intermancer.gaiaf.core.organism.gene.basic;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubtractionGeneTest {

    @Test
    public void testSubtractionOperation() {
        SubtractionGene gene = new SubtractionGene();

        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(10.0);

        gene.consume(dataQuantum);

        assertEquals(8.5, dataQuantum.getValue(1), 0.0001);
    }
}