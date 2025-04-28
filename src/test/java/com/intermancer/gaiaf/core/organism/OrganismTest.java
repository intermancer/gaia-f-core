package com.intermancer.gaiaf.core.organism;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrganismTest {
    
    @Test
    public void testOrganismConstruction() {
        Organism organism = new Organism("test-organism");
        assertEquals("test-organism", organism.getId());
        assertNotNull(organism.getChromosomes());
        assertEquals(0, organism.getChromosomes().size());
    }
    
    @Test
    public void testAddChromosome() {
        Organism organism = new Organism("organism-1");
        Chromosome chromosome = new Chromosome();
        organism.addChromosome(chromosome);
        
        assertEquals(1, organism.getChromosomes().size());
        assertEquals(chromosome, organism.getChromosomes().get(0));
        
        // Test that null chromosomes are rejected
        assertThrows(IllegalArgumentException.class, () -> organism.addChromosome(null));
    }
    
    @Test
    public void testGetChromosomes() {
        Organism organism = new Organism("test-organism");
        Chromosome chromosome1 = new Chromosome();
        Chromosome chromosome2 = new Chromosome();
        
        organism.addChromosome(chromosome1);
        organism.addChromosome(chromosome2);
        
        assertEquals(2, organism.getChromosomes().size());
        assertEquals(chromosome1, organism.getChromosomes().get(0));
        assertEquals(chromosome2, organism.getChromosomes().get(1));
    }
    
    @Test
    public void testOrganismWithNoChromosomes() {
        Organism organism = new Organism("empty-organism");
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(10.0);
        
        // Consuming with no chromosomes should leave the DataQuantum unchanged
        organism.consume(dataQuantum);
        assertEquals(10.0, dataQuantum.getValue(0));
        assertEquals(10.0, dataQuantum.getDataPoint(0).getValue());
    }
    
    @Test
    public void testOrganismWithSingleChromosome() {
        Organism organism = new Organism("single-chromosome-organism");
        Chromosome chromosome = new Chromosome();
        
        // Add a gene to the chromosome that triples the value
        chromosome.getGenes().add(new TestGenes.MultiplierGene(3.0, 0, "triple-gene"));
        
        organism.addChromosome(chromosome);
        
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(4.0);
        
        organism.consume(dataQuantum);
        assertEquals(12.0, dataQuantum.getValue(1));
    }
    
    @Test
    public void testOrganismWithMultipleChromosomes() {
        Organism organism = new Organism("multi-chromosome-organism");
        
        // First chromosome with a gene that doubles the value
        Chromosome chromosome1 = new Chromosome();
        chromosome1.getGenes().add(new TestGenes.MultiplierGene(2.0, 0, "double-gene"));
        
        // Second chromosome with a gene that adds 5
        Chromosome chromosome2 = new Chromosome();
        chromosome2.getGenes().add(new TestGenes.AdderGene(5.0, 0, "add5-gene"));
        
        organism.addChromosome(chromosome1);
        organism.addChromosome(chromosome2);
        
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(3.0);
        
        organism.consume(dataQuantum);
        
        // Check results: original value, doubled value from first chromosome, value+5 from second chromosome
        assertEquals(3.0, dataQuantum.getValue(0));
        assertEquals(6.0, dataQuantum.getValue(1));
        assertEquals(8.0, dataQuantum.getValue(2));
    }
    
    @Test
    public void testComplexOrganismStructure() {
        // Create a complex organism with multiple chromosomes and genes
        Organism organism = new Organism("complex-organism");
        
        // First chromosome: double and then add 1
        Chromosome chromosome1 = new Chromosome();
        
        chromosome1.getGenes().add(new TestGenes.MultiplierGene(2.0, 0, "double-gene"));
        chromosome1.getGenes().add(new TestGenes.AdderGene(1.0, 1, "add1-gene"));
        
        // Second chromosome: square the original value
        Chromosome chromosome2 = new Chromosome();
        
        chromosome2.getGenes().add(new TestGenes.SquareGene(0, "square-gene"));
        
        organism.addChromosome(chromosome1);
        organism.addChromosome(chromosome2);
        
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(4.0);
        
        organism.consume(dataQuantum);
        
        // Verify the results: 4.0 (original) -> 8.0 (doubled) -> 9.0 (8+1) -> 16.0 (4^2)
        assertEquals(4.0, dataQuantum.getValue(0));
        assertEquals(8.0, dataQuantum.getValue(1));
        assertEquals(9.0, dataQuantum.getValue(2));
        assertEquals(16.0, dataQuantum.getValue(3));
    }
    
    @Test
    public void testChromosomeOrderMatters() {
        // Test that chromosomes are processed in the order they were added
        Organism organism = new Organism("order-test-organism");
        
        // First chromosome: add 5
        Chromosome chromosome1 = new Chromosome();
        chromosome1.getGenes().add(new TestGenes.AdderGene(5.0, 0, "add5-gene"));
        
        // Second chromosome: multiply by 2
        Chromosome chromosome2 = new Chromosome();
        chromosome2.getGenes().add(new TestGenes.MultiplierGene(2.0, 0, "double-gene"));
        
        organism.addChromosome(chromosome1);
        organism.addChromosome(chromosome2);
        
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(3.0);
        
        organism.consume(dataQuantum);
        
        // Verify the order: original (3.0) -> +5 = 8.0 -> *2 = 6.0
        assertEquals(3.0, dataQuantum.getValue(0));
        assertEquals(8.0, dataQuantum.getValue(1));
        assertEquals(6.0, dataQuantum.getValue(2));
    }
}