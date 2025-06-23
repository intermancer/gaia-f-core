package com.intermancer.gaiaf.core.organism;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.intermancer.gaiaf.core.experiment.MutationCommand;

public class OrganismTest {
    
    @Test
    public void testOrganismConstruction() {
        Organism organism = new Organism("test-organism");
        assertEquals("test-organism", organism.getId());
        assertNotNull(organism.getChromosomes());
        assertEquals(0, organism.getChromosomes().size());
    }
    
    @Test
    public void testDefaultConstructor() {
        Organism organism = new Organism();
        assertNotNull(organism.getChromosomes());
        assertEquals(0, organism.getChromosomes().size());
        assertNull(organism.getId());
    }
    
    @Test
    public void testAddChromosome() {
        Organism organism = new Organism("organism-1");
        Chromosome chromosome = new Chromosome();
        organism.addChromosome(chromosome);
        
        assertEquals(1, organism.getChromosomes().size());
        assertEquals(chromosome, organism.getChromosomes().get(0));
        
        // Test that null chromosomes are rejected
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> organism.addChromosome(null));
        assertEquals("Chromosome cannot be null", exception.getMessage());
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
    public void testSetChromosomes() {
        Organism organism = new Organism("test-organism");
        
        Chromosome chromosome1 = new Chromosome();
        Chromosome chromosome2 = new Chromosome();
        List<Chromosome> chromosomes = List.of(chromosome1, chromosome2);
        
        organism.setChromosomes(chromosomes);
        
        assertEquals(2, organism.getChromosomes().size());
        assertEquals(chromosome1, organism.getChromosomes().get(0));
        assertEquals(chromosome2, organism.getChromosomes().get(1));
        
        // Test setting null list
        organism.setChromosomes(null);
        assertEquals(0, organism.getChromosomes().size());
        
        // Test setting empty list
        organism.setChromosomes(new ArrayList<>());
        assertEquals(0, organism.getChromosomes().size());
    }
    
    @Test
    public void testSetAndGetId() {
        Organism organism = new Organism();
        assertNull(organism.getId());
        
        organism.setId("new-id");
        assertEquals("new-id", organism.getId());
        
        organism.setId(null);
        assertNull(organism.getId());
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
    
    @Test
    public void testEquals() {
        // Create two organisms with the same chromosomes
        Organism organism1 = new Organism("organism1");
        Chromosome chromosome1a = new Chromosome();
        chromosome1a.getGenes().add(new TestGenes.AdderGene(5.0, 0, "gene1"));
        organism1.addChromosome(chromosome1a);
        
        Organism organism2 = new Organism("organism2");
        Chromosome chromosome2a = new Chromosome();
        chromosome2a.getGenes().add(new TestGenes.AdderGene(5.0, 0, "gene1"));
        organism2.addChromosome(chromosome2a);
        
        // Should be equal (ID is not considered in equals)
        assertTrue(organism1.equals(organism2));
        
        // Test reflexivity
        assertTrue(organism1.equals(organism1));
        
        // Test with different chromosomes
        Organism organism3 = new Organism("organism3");
        Chromosome chromosome3a = new Chromosome();
        chromosome3a.getGenes().add(new TestGenes.AdderGene(3.0, 0, "gene3"));
        organism3.addChromosome(chromosome3a);
        
        assertFalse(organism1.equals(organism3));
        
        // Test empty organisms
        Organism emptyOrganism1 = new Organism("empty1");
        Organism emptyOrganism2 = new Organism("empty2");
        assertTrue(emptyOrganism1.equals(emptyOrganism2));
    }
    
    @Test
    public void testHashCode() {
        Organism organism1 = new Organism("organism1");
        Chromosome chromosome1 = new Chromosome();
        chromosome1.getGenes().add(new TestGenes.AdderGene(5.0, 0, "gene1"));
        organism1.addChromosome(chromosome1);
        
        Organism organism2 = new Organism("organism2");
        Chromosome chromosome2 = new Chromosome();
        chromosome2.getGenes().add(new TestGenes.AdderGene(5.0, 0, "gene1"));
        organism2.addChromosome(chromosome2);
        
        // Equal organisms should have equal hash codes
        assertEquals(organism1.hashCode(), organism2.hashCode());
    }
    
    // Tests for Mutation behavior
    
    @Test
    public void testGetMutationCommandListEmptyOrganism() {
        Organism emptyOrganism = new Organism("empty");
        
        List<MutationCommand> mutations = emptyOrganism.getMutationCommandList();
        
        // Should have only the "add random chromosome" mutation
        assertEquals(1, mutations.size());
        
        long addMutations = mutations.stream()
            .filter(m -> m.getDescription().contains("Add a random chromosome"))
            .count();
        assertEquals(1, addMutations);
        
        long exchangeMutations = mutations.stream()
            .filter(m -> m.getDescription().contains("Reorder a single chromosome"))
            .count();
        assertEquals(0, exchangeMutations);
        
        long deleteMutations = mutations.stream()
            .filter(m -> m.getDescription().contains("Delete a random chromosome"))
            .count();
        assertEquals(0, deleteMutations);
    }
    
    @Test
    public void testGetMutationCommandListSingleChromosome() {
        Organism organism = new Organism("single");
        Chromosome chromosome = new Chromosome();
        chromosome.getGenes().add(new TestGenes.AdderGene(5.0, 0, "gene1"));
        organism.addChromosome(chromosome);
        
        List<MutationCommand> mutations = organism.getMutationCommandList();
        
        // Should have only "add" mutation, no "exchange" or "delete" mutations
        long addMutations = mutations.stream()
            .filter(m -> m.getDescription().contains("Add a random chromosome"))
            .count();
        assertEquals(1, addMutations);
        
        long exchangeMutations = mutations.stream()
            .filter(m -> m.getDescription().contains("Reorder a single chromosome"))
            .count();
        assertEquals(0, exchangeMutations);
        
        long deleteMutations = mutations.stream()
            .filter(m -> m.getDescription().contains("Delete a random chromosome"))
            .count();
        assertEquals(0, deleteMutations);
        
        // Should also include mutations from the chromosome and its genes
        assertEquals(6, mutations.size()); // At least organism mutations + chromosome mutations + gene mutations
    }
    
    @Test
    public void testGetMutationCommandListMultipleChromosomes() {
        Organism organism = new Organism("multiple");
        
        Chromosome chromosome1 = new Chromosome();
        chromosome1.getGenes().add(new TestGenes.AdderGene(5.0, 0, "gene1"));
        
        Chromosome chromosome2 = new Chromosome();
        chromosome2.getGenes().add(new TestGenes.MultiplierGene(2.0, 0, "gene2"));
        
        organism.addChromosome(chromosome1);
        organism.addChromosome(chromosome2);
        
        List<MutationCommand> mutations = organism.getMutationCommandList();
        
        // Should have all three types of organism mutations
        long addMutations = mutations.stream()
            .filter(m -> m.getDescription().contains("Add a random chromosome"))
            .count();
        assertEquals(1, addMutations);
        
        long exchangeMutations = mutations.stream()
            .filter(m -> m.getDescription().contains("Reorder a single chromosome"))
            .count();
        assertEquals(1, exchangeMutations);
        
        long deleteMutations = mutations.stream()
            .filter(m -> m.getDescription().contains("Delete a random chromosome"))
            .count();
        assertEquals(1, deleteMutations);
        
        // Should also include mutations from both chromosomes and their genes
        assertEquals(13, mutations.size()); // 3 organism mutations + mutations from 2 chromosomes + gene mutations
    }
    
    @Test
    public void testExchangeChromosomeMutation() {
        Organism organism = new Organism("exchange-test");
        
        Chromosome chromosome1 = new Chromosome();
        chromosome1.getGenes().add(new TestGenes.AdderGene(1.0, 0, "gene1"));
        
        Chromosome chromosome2 = new Chromosome();
        chromosome2.getGenes().add(new TestGenes.MultiplierGene(2.0, 0, "gene2"));
        
        Chromosome chromosome3 = new Chromosome();
        chromosome3.getGenes().add(new TestGenes.AdderGene(3.0, 0, "gene3"));
        
        organism.addChromosome(chromosome1);
        organism.addChromosome(chromosome2);
        organism.addChromosome(chromosome3);
        
        List<Chromosome> originalOrder = List.copyOf(organism.getChromosomes());
        
        // Find exchange mutation
        MutationCommand exchangeMutation = organism.getMutationCommandList().stream()
            .filter(m -> m.getDescription().contains("Reorder a single chromosome"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(exchangeMutation);
        
        // Execute mutation multiple times until we see a change (since it's random)
        boolean orderChanged = false;
        for (int i = 0; i < 50; i++) {
            organism.getChromosomes().clear();
            organism.getChromosomes().addAll(originalOrder);
            
            exchangeMutation.execute();
            
            if (!organism.getChromosomes().equals(originalOrder)) {
                orderChanged = true;
                break;
            }
        }
        
        assertTrue(orderChanged, "Exchange mutation should eventually change chromosome order");
        assertEquals(3, organism.getChromosomes().size(), "Chromosome count should remain the same");
    }
    
    @Test
    public void testDeleteChromosomeMutation() {
        Organism organism = new Organism("delete-test");
        organism.addChromosome(new Chromosome());
        organism.addChromosome(new Chromosome());
        
        int originalSize = organism.getChromosomes().size();
        
        // Find delete mutation
        MutationCommand deleteMutation = organism.getMutationCommandList().stream()
            .filter(m -> m.getDescription().contains("Delete a random chromosome"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(deleteMutation);
        
        deleteMutation.execute();
        
        assertEquals(originalSize - 1, organism.getChromosomes().size());
    }
    
    @Test
    public void testMutationCommandsHaveDescriptions() {
        Organism organism = new Organism("description-test");
        organism.addChromosome(new Chromosome());
        organism.addChromosome(new Chromosome());
        
        List<MutationCommand> mutations = organism.getMutationCommandList();
        
        // Test that all mutations have non-empty descriptions
        for (MutationCommand mutation : mutations) {
            assertNotNull(mutation.getDescription());
            assertFalse(mutation.getDescription().trim().isEmpty());
        }
    }
    
    @Test
    public void testMutationCommandsAreExecutable() {
        Organism organism = new Organism("executable-test");
        
        Chromosome chromosome = new Chromosome();
        chromosome.getGenes().add(new TestGenes.AdderGene(1.0, 0, "gene1"));
        organism.addChromosome(chromosome);
        organism.addChromosome(new Chromosome());
        
        List<MutationCommand> mutations = organism.getMutationCommandList();
        
        // Test that all mutations can be executed without throwing exceptions
        for (MutationCommand mutation : mutations) {
            assertDoesNotThrow(() -> mutation.execute());
        }
    }
    
    @Test
    public void testMutationIncludesChromosomeMutations() {
        Organism organism = new Organism("include-test");
        
        Chromosome chromosome = new Chromosome();
        chromosome.getGenes().add(new TestGenes.AdderGene(1.0, 0, "gene1"));
        organism.addChromosome(chromosome);
        
        List<MutationCommand> organismMutations = organism.getMutationCommandList();
        List<MutationCommand> chromosomeMutations = chromosome.getMutationCommandList();
        
        // Organism mutations should include all chromosome mutations plus its own
        assertTrue(organismMutations.size() >= chromosomeMutations.size() + 1); // At least chromosome mutations + add
        
        // Check that chromosome mutation descriptions are included
        long chromosomeMutationCount = organismMutations.stream()
            .filter(m -> m.getDescription().contains("gene") || 
                        m.getDescription().contains("targetIndex") || 
                        m.getDescription().contains("operationConstant"))
            .count();
        
        assertTrue(chromosomeMutationCount >= chromosomeMutations.size() - 3); // Most chromosome mutations should be included
    }
    
    @Test
    public void testConsistentMutationBehavior() {
        Organism organism = new Organism("consistent-test");
        
        Chromosome chromosome = new Chromosome();
        chromosome.getGenes().add(new TestGenes.AdderGene(1.0, 0, "gene1"));
        organism.addChromosome(chromosome);
        organism.addChromosome(new Chromosome());
        
        // Test that calling getMutationCommandList multiple times returns consistent results
        List<MutationCommand> mutations1 = organism.getMutationCommandList();
        List<MutationCommand> mutations2 = organism.getMutationCommandList();
        
        assertEquals(mutations1.size(), mutations2.size());
        
        // Test that mutation descriptions are consistent
        for (int i = 0; i < mutations1.size(); i++) {
            assertEquals(mutations1.get(i).getDescription(), mutations2.get(i).getDescription());
        }
    }
    
    @Test
    public void testMutationHierarchy() {
        // Test that organism contains chromosome mutations which contain gene mutations
        Organism organism = new Organism("hierarchy-test");
        
        Chromosome chromosome = new Chromosome();
        Gene gene = new TestGenes.AdderGene(1.0, 0, "gene1");
        chromosome.getGenes().add(gene);
        organism.addChromosome(chromosome);
        
        List<MutationCommand> organismMutations = organism.getMutationCommandList();
        List<MutationCommand> chromosomeMutations = chromosome.getMutationCommandList();
        List<MutationCommand> geneMutations = gene.getMutationCommandList();
        
        // Organism should have the most mutations
        assertTrue(organismMutations.size() > chromosomeMutations.size());
        assertTrue(chromosomeMutations.size() > geneMutations.size());
        
        // Gene mutations should be included in chromosome mutations
        for (MutationCommand geneMutation : geneMutations) {
            boolean found = chromosomeMutations.stream()
                .anyMatch(cm -> cm.getDescription().equals(geneMutation.getDescription()));
            assertTrue(found, "Gene mutation should be included in chromosome mutations: " + geneMutation.getDescription());
        }
    }
}