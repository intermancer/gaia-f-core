package com.intermancer.gaiaf.core.organism;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChromosomeTest {
    
    @Test
    public void testChromosomeConstruction() {
        Chromosome chromosome = new Chromosome();
        assertNotNull(chromosome);
        assertNotNull(chromosome.getId());
        assertTrue(chromosome.getId().startsWith("Chromosome-"));
    }
    
    @Test
    public void testChromosomeWithNoGenes() {
        Chromosome chromosome = new Chromosome();
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(5.0);
        
        // Consuming with no genes should leave the DataQuantum unchanged
        chromosome.consume(dataQuantum);
        assertEquals(1, dataQuantum.getDataPoint(0).getValue() == 5.0 ? 1 : 0);
    }
    
    @Test
    public void testChromosomeWithSingleGene() {
        Chromosome chromosome = new Chromosome();
        
        // Add a gene that doubles the value
        chromosome.genes.add(new TestGenes.MultiplierGene(2.0, 0, "double-gene"));
        
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(3.0);
        
        chromosome.consume(dataQuantum);
        assertEquals(2, dataQuantum.getDataPoint(1).getValue() == 6.0 ? 2 : 0);
    }
    
    @Test
    public void testChromosomeWithMultipleGenes() {
        Chromosome chromosome = new Chromosome();
        
        // Add a gene that doubles the value
        chromosome.genes.add(new TestGenes.MultiplierGene(2.0, 0, "double-gene"));
        
        // Add a gene that adds 10
        chromosome.genes.add(new TestGenes.AdderGene(10.0, 0, "add10-gene"));
        
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(5.0);
        
        chromosome.consume(dataQuantum);
        
        // Check results: original value, doubled value, value+10
        assertEquals(5.0, dataQuantum.getValue(0));
        assertEquals(10.0, dataQuantum.getValue(1));
        assertEquals(15.0, dataQuantum.getValue(2));
    }
    
    @Test
    public void testChromosomeGenesInteraction() {
        Chromosome chromosome = new Chromosome();
        
        // First gene doubles the initial value
        chromosome.genes.add(new TestGenes.MultiplierGene(2.0, 0, "gene1"));
        
        // Second gene takes the result of the first gene and adds 1
        chromosome.genes.add(new TestGenes.AdderGene(1.0, 1, "gene2"));
        
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(3.0);
        
        chromosome.consume(dataQuantum);
        
        // Verify the chain of operations: 3.0 -> 6.0 -> 7.0
        assertEquals(3.0, dataQuantum.getValue(0)); // Original value
        assertEquals(6.0, dataQuantum.getValue(1)); // After first gene
        assertEquals(7.0, dataQuantum.getValue(2)); // After second gene
    }
}
