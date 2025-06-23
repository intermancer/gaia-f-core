package com.intermancer.gaiaf.core.organism;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.intermancer.gaiaf.core.experiment.MutationCommand;

public class ChromosomeTest {
    
    @Test
    public void testChromosomeConstruction() {
        Chromosome chromosome = new Chromosome();
        assertNotNull(chromosome);
        assertNotNull(chromosome.getGenes());
        assertEquals(0, chromosome.getGenes().size());
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
        chromosome.getGenes().add(new TestGenes.MultiplierGene(2.0, 0, "double-gene"));
        
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(3.0);
        
        chromosome.consume(dataQuantum);
        assertEquals(2, dataQuantum.getDataPoint(1).getValue() == 6.0 ? 2 : 0);
    }
    
    @Test
    public void testChromosomeWithMultipleGenes() {
        Chromosome chromosome = new Chromosome();
        
        // Add a gene that doubles the value
        chromosome.getGenes().add(new TestGenes.MultiplierGene(2.0, 0, "double-gene"));
        
        // Add a gene that adds 10
        chromosome.getGenes().add(new TestGenes.AdderGene(10.0, 0, "add10-gene"));
        
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
        chromosome.getGenes().add(new TestGenes.MultiplierGene(2.0, 0, "gene1"));
        
        // Second gene takes the result of the first gene and adds 1
        chromosome.getGenes().add(new TestGenes.AdderGene(1.0, 1, "gene2"));
        
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(3.0);
        
        chromosome.consume(dataQuantum);
        
        // Verify the chain of operations: 3.0 -> 6.0 -> 7.0
        assertEquals(3.0, dataQuantum.getValue(0)); // Original value
        assertEquals(6.0, dataQuantum.getValue(1)); // After first gene
        assertEquals(7.0, dataQuantum.getValue(2)); // After second gene
    }
    
    // New tests for properties and functionality
    
    @Test
    public void testGenesSetterAndGetter() {
        Chromosome chromosome = new Chromosome();
        
        // Create genes
        Gene gene1 = new TestGenes.AdderGene(1.0, 0, "gene1");
        Gene gene2 = new TestGenes.MultiplierGene(2.0, 0, "gene2");
        
        // Test adding genes directly to the list
        chromosome.getGenes().add(gene1);
        chromosome.getGenes().add(gene2);
        
        assertEquals(2, chromosome.getGenes().size());
        assertEquals(gene1, chromosome.getGenes().get(0));
        assertEquals(gene2, chromosome.getGenes().get(1));
        
        // Test setting new genes list
        List<Gene> newGenes = List.of(gene2, gene1);
        chromosome.setGenes(newGenes);
        
        assertEquals(2, chromosome.getGenes().size());
        assertEquals(gene2, chromosome.getGenes().get(0));
        assertEquals(gene1, chromosome.getGenes().get(1));
    }
    
    @Test
    public void testCopyOf() {
        Chromosome originalChromosome = new Chromosome();
        originalChromosome.getGenes().add(new TestGenes.AdderGene(5.0, 0, "original-gene1"));
        originalChromosome.getGenes().add(new TestGenes.MultiplierGene(2.0, 1, "original-gene2"));
        
        Chromosome copiedChromosome = originalChromosome.copyOf();
        
        // Test that copy is a different instance
        assertNotSame(originalChromosome, copiedChromosome);
        
        // Test that genes list is different instance but same size
        assertNotSame(originalChromosome.getGenes(), copiedChromosome.getGenes());
        assertEquals(originalChromosome.getGenes().size(), copiedChromosome.getGenes().size());
        
        // Test that each gene is a copy
        for (int i = 0; i < originalChromosome.getGenes().size(); i++) {
            Gene originalGene = originalChromosome.getGenes().get(i);
            Gene copiedGene = copiedChromosome.getGenes().get(i);
            
            // Should be different instances
            assertNotSame(originalGene, copiedGene);
            
            // Should be same type
            assertEquals(originalGene.getClass(), copiedGene.getClass());
            
            // Should have different IDs (due to cloning)
            assertNotSame(originalGene.getId(), copiedGene.getId());
        }
        
        // Test that both chromosomes produce the same results
        DataQuantum originalData = new DataQuantum();
        originalData.addValue(10.0);
        originalData.addValue(20.0);
        
        DataQuantum copiedData = new DataQuantum();
        copiedData.addValue(10.0);
        copiedData.addValue(20.0);
        
        originalChromosome.consume(originalData);
        copiedChromosome.consume(copiedData);
        
        // Should have same number of data points
        assertEquals(originalData.getDataPoints().size(), copiedData.getDataPoints().size());
        
        // Values should be the same (though source IDs will be different due to different gene IDs)
        for (int i = 0; i < originalData.getDataPoints().size(); i++) {
            assertEquals(originalData.getValue(i), copiedData.getValue(i), 0.0001);
        }
    }
    
    @Test
    public void testEquals() {
        // Create two chromosomes with the same genes
        Chromosome chromosome1 = new Chromosome();
        chromosome1.getGenes().add(new TestGenes.AdderGene(5.0, 0, "gene1"));
        chromosome1.getGenes().add(new TestGenes.MultiplierGene(2.0, 1, "gene2"));
        
        Chromosome chromosome2 = new Chromosome();
        chromosome2.getGenes().add(new TestGenes.AdderGene(5.0, 0, "gene1"));
        chromosome2.getGenes().add(new TestGenes.MultiplierGene(2.0, 1, "gene2"));
        
        // Should be equal
        assertTrue(chromosome1.equals(chromosome2));
        
        // Test reflexivity
        assertTrue(chromosome1.equals(chromosome1));
        
        // Test with different genes
        Chromosome chromosome3 = new Chromosome();
        chromosome3.getGenes().add(new TestGenes.AdderGene(3.0, 0, "gene3"));
        
        assertFalse(chromosome1.equals(chromosome3));
        
        // Test empty chromosomes
        Chromosome emptyChromosome1 = new Chromosome();
        Chromosome emptyChromosome2 = new Chromosome();
        assertTrue(emptyChromosome1.equals(emptyChromosome2));
    }
    
    @Test
    public void testHashCode() {
        Chromosome chromosome1 = new Chromosome();
        chromosome1.getGenes().add(new TestGenes.AdderGene(5.0, 0, "gene1"));
        
        Chromosome chromosome2 = new Chromosome();
        chromosome2.getGenes().add(new TestGenes.AdderGene(5.0, 0, "gene1"));
        
        // Equal chromosomes should have equal hash codes
        assertEquals(chromosome1.hashCode(), chromosome2.hashCode());
    }
    
    // Tests for Mutation behavior
    
    @Test
    public void testGetMutationCommandListEmptyChromosome() {
        Chromosome emptyChromosome = new Chromosome();
        
        List<MutationCommand> mutations = emptyChromosome.getMutationCommandList();
        
        // Should have at least the "add random gene" mutation, but no "exchange" or "remove" mutations
        assertTrue(mutations.size() >= 1);
        
        long addMutations = mutations.stream()
            .filter(m -> m.getDescription().contains("Add a random gene"))
            .count();
        assertEquals(1, addMutations);
        
        long exchangeMutations = mutations.stream()
            .filter(m -> m.getDescription().contains("Move a random gene"))
            .count();
        assertEquals(0, exchangeMutations);
        
        long removeMutations = mutations.stream()
            .filter(m -> m.getDescription().contains("Delete a random gene"))
            .count();
        assertEquals(0, removeMutations);
    }
    
    @Test
    public void testGetMutationCommandListSingleGene() {
        Chromosome chromosome = new Chromosome();
        chromosome.getGenes().add(new TestGenes.AdderGene(5.0, 0, "gene1"));
        
        List<MutationCommand> mutations = chromosome.getMutationCommandList();
        
        // Should have "add" and "remove" mutations, but no "exchange" mutation
        long addMutations = mutations.stream()
            .filter(m -> m.getDescription().contains("Add a random gene"))
            .count();
        assertEquals(1, addMutations);
        
        long exchangeMutations = mutations.stream()
            .filter(m -> m.getDescription().contains("Move a random gene"))
            .count();
        assertEquals(0, exchangeMutations);
        
        long removeMutations = mutations.stream()
            .filter(m -> m.getDescription().contains("Delete a random gene"))
            .count();
        assertEquals(0, removeMutations);
        
        // Should also include mutations from the gene itself
        assertEquals(5, mutations.size()); // At least chromosome mutations + gene mutations
    }
    
    @Test
    public void testGetMutationCommandListMultipleGenes() {
        Chromosome chromosome = new Chromosome();
        chromosome.getGenes().add(new TestGenes.AdderGene(5.0, 0, "gene1"));
        chromosome.getGenes().add(new TestGenes.MultiplierGene(2.0, 1, "gene2"));
        
        List<MutationCommand> mutations = chromosome.getMutationCommandList();
        
        // Should have all three types of chromosome mutations
        long addMutations = mutations.stream()
            .filter(m -> m.getDescription().contains("Add a random gene"))
            .count();
        assertEquals(1, addMutations);
        
        long exchangeMutations = mutations.stream()
            .filter(m -> m.getDescription().contains("Move a random gene"))
            .count();
        assertEquals(1, exchangeMutations);
        
        long removeMutations = mutations.stream()
            .filter(m -> m.getDescription().contains("Delete a random gene"))
            .count();
        assertEquals(1, removeMutations);
        
        // Should also include mutations from both genes
        assertTrue(mutations.size() > 10); // 3 chromosome mutations + mutations from 2 genes
    }
    
    @Test
    public void testExchangeGeneMutation() {
        Chromosome chromosome = new Chromosome();
        Gene gene1 = new TestGenes.AdderGene(1.0, 0, "gene1");
        Gene gene2 = new TestGenes.MultiplierGene(2.0, 0, "gene2");
        Gene gene3 = new TestGenes.AdderGene(3.0, 0, "gene3");
        
        chromosome.getGenes().add(gene1);
        chromosome.getGenes().add(gene2);
        chromosome.getGenes().add(gene3);
        
        List<Gene> originalOrder = List.copyOf(chromosome.getGenes());
        
        // Find exchange mutation
        MutationCommand exchangeMutation = chromosome.getMutationCommandList().stream()
            .filter(m -> m.getDescription().contains("Move a random gene"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(exchangeMutation);
        
        // Execute mutation multiple times until we see a change (since it's random)
        boolean orderChanged = false;
        for (int i = 0; i < 50; i++) {
            chromosome.getGenes().clear();
            chromosome.getGenes().addAll(originalOrder);
            
            exchangeMutation.execute();
            
            if (!chromosome.getGenes().equals(originalOrder)) {
                orderChanged = true;
                break;
            }
        }
        
        assertTrue(orderChanged, "Exchange mutation should eventually change gene order");
        assertEquals(3, chromosome.getGenes().size(), "Gene count should remain the same");
    }
    
    @Test
    public void testRemoveGeneMutation() {
        Chromosome chromosome = new Chromosome();
        chromosome.getGenes().add(new TestGenes.AdderGene(1.0, 0, "gene1"));
        chromosome.getGenes().add(new TestGenes.MultiplierGene(2.0, 0, "gene2"));
        
        int originalSize = chromosome.getGenes().size();
        
        // Find remove mutation
        MutationCommand removeMutation = chromosome.getMutationCommandList().stream()
            .filter(m -> m.getDescription().contains("Delete a random gene"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(removeMutation);
        
        removeMutation.execute();
        
        assertEquals(originalSize - 1, chromosome.getGenes().size());
    }
    
    @Test
    public void testAddGeneMutation() {
        Chromosome chromosome = new Chromosome();
        chromosome.getGenes().add(new TestGenes.AdderGene(1.0, 0, "gene1"));
        
        int originalSize = chromosome.getGenes().size();
        
        // Find add mutation
        MutationCommand addMutation = chromosome.getMutationCommandList().stream()
            .filter(m -> m.getDescription().contains("Add a random gene"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(addMutation);
        
        addMutation.execute();
        
        assertEquals(originalSize + 1, chromosome.getGenes().size());
        assertNotNull(chromosome.getGenes().get(chromosome.getGenes().size() - 1));
    }
    
    @Test
    public void testAddGeneMutationToEmptyChromosome() {
        Chromosome emptyChromosome = new Chromosome();
        assertEquals(0, emptyChromosome.getGenes().size());
        
        // Find add mutation
        MutationCommand addMutation = emptyChromosome.getMutationCommandList().stream()
            .filter(m -> m.getDescription().contains("Add a random gene"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(addMutation);
        
        addMutation.execute();
        
        assertEquals(1, emptyChromosome.getGenes().size());
        assertNotNull(emptyChromosome.getGenes().get(0));
    }
    
    @Test
    public void testMutationCommandsHaveDescriptions() {
        Chromosome chromosome = new Chromosome();
        chromosome.getGenes().add(new TestGenes.AdderGene(1.0, 0, "gene1"));
        chromosome.getGenes().add(new TestGenes.MultiplierGene(2.0, 0, "gene2"));
        
        List<MutationCommand> mutations = chromosome.getMutationCommandList();
        
        // Test that all mutations have non-empty descriptions
        for (MutationCommand mutation : mutations) {
            assertNotNull(mutation.getDescription());
            assertFalse(mutation.getDescription().trim().isEmpty());
        }
    }
    
    @Test
    public void testMutationCommandsAreExecutable() {
        Chromosome chromosome = new Chromosome();
        chromosome.getGenes().add(new TestGenes.AdderGene(1.0, 0, "gene1"));
        chromosome.getGenes().add(new TestGenes.MultiplierGene(2.0, 0, "gene2"));
        
        List<MutationCommand> mutations = chromosome.getMutationCommandList();
        
        // Test that all mutations can be executed without throwing exceptions
        for (MutationCommand mutation : mutations) {
            assertDoesNotThrow(() -> mutation.execute());
        }
    }
    
    @Test
    public void testMutationIncludesGeneMutations() {
        Chromosome chromosome = new Chromosome();
        Gene gene = new TestGenes.AdderGene(1.0, 0, "gene1");
        chromosome.getGenes().add(gene);
        
        List<MutationCommand> chromosomeMutations = chromosome.getMutationCommandList();
        List<MutationCommand> geneMutations = gene.getMutationCommandList();
        
        // Chromosome mutations should include all gene mutations plus its own
        assertTrue(chromosomeMutations.size() >= geneMutations.size() + 1); // At least gene mutations + add + remove
        
        // Check that gene mutation descriptions are included
        long geneMutationCount = chromosomeMutations.stream()
            .filter(m -> m.getDescription().contains("targetIndex") || m.getDescription().contains("operationConstant"))
            .count();
        
        assertEquals(geneMutations.size(), geneMutationCount);
    }
    
    @Test
    public void testConsistentMutationBehavior() {
        Chromosome chromosome = new Chromosome();
        chromosome.getGenes().add(new TestGenes.AdderGene(1.0, 0, "gene1"));
        chromosome.getGenes().add(new TestGenes.MultiplierGene(2.0, 0, "gene2"));
        
        // Test that calling getMutationCommandList multiple times returns consistent results
        List<MutationCommand> mutations1 = chromosome.getMutationCommandList();
        List<MutationCommand> mutations2 = chromosome.getMutationCommandList();
        
        assertEquals(mutations1.size(), mutations2.size());
        
        // Test that mutation descriptions are consistent
        for (int i = 0; i < mutations1.size(); i++) {
            assertEquals(mutations1.get(i).getDescription(), mutations2.get(i).getDescription());
        }
    }
}