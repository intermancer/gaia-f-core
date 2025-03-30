package com.intermancer.gaiaf.core.organism;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GeneTest {
    
    @Test
    public void testGeneAbstractClass() {
        // Use AdderGene for testing
        Gene testGene = new TestGenes.AdderGene(5, 0, "test-gene");
        
        // Test the getId method
        assertEquals("test-gene", testGene.getId());
        
        // Test the consume method
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(10.0);
        testGene.consume(dataQuantum);
        assertEquals(15.0, dataQuantum.getValue(1));
    }
    
    @Test
    public void testMultipleGeneOperations() {
        // Gene that squares the value
        Gene squareGene = new TestGenes.SquareGene(0, "square-gene");
        
        // Gene that takes the square root
        Gene sqrtGene = new TestGenes.SquareRootGene(0, "sqrt-gene");
        
        // Create a DataQuantum to test with
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(4.0);
        
        // Apply the genes in sequence
        squareGene.consume(dataQuantum);
        sqrtGene.consume(dataQuantum);
        
        // Verify results
        assertEquals(4.0, dataQuantum.getValue(0)); // Original value
        assertEquals(16.0, dataQuantum.getValue(1)); // After square
        assertEquals(2.0, dataQuantum.getValue(2)); // After sqrt
    }
    
    @Test
    public void testDataQuantumSourceId() {
        // Create a Gene that sets a specific sourceId
        Gene sourceIdGene = new TestGenes.SourceIdGene(0, 3, "source-id-gene");
        
        // Create a DataQuantum to test with
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(7.0);
        
        // Apply the gene
        sourceIdGene.consume(dataQuantum);
        
        // Verify both the value and sourceId
        assertEquals(21.0, dataQuantum.getDataPoint(1).getValue());
        assertEquals("source-id-gene", dataQuantum.getDataPoint(1).getSourceId());
    }
}
