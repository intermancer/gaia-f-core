package com.intermancer.gaiaf.core.organism;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.intermancer.gaiaf.core.experiment.MutationCommand;

public class GeneTest {
    
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
    
    // New tests for properties and initialization
    
    @Test
    public void testGeneInitialization() {
        Gene gene = new TestGenes.AdderGene(1.0, 0, "init-test");
        
        // Test default targetIndexList initialization
        assertNotNull(gene.getTargetIndexList());
        assertEquals(1, gene.getTargetIndexList().size());
        assertEquals(0, gene.getTargetIndexList().get(0)); // Should be set by constructor
        
        // Test operationConstantList initialization
        assertNotNull(gene.getOperationConstantList());
        assertEquals(1, gene.getOperationConstantList().size());
        assertEquals(1.0, gene.getOperationConstantList().get(0));
        
        // Test ID
        assertEquals("init-test", gene.getId());
    }
    
    @Test
    public void testGetIdWithNullId() {
        Gene gene = new TestGenes.AdderGene(1.0, 0, null);
        gene.setId(null);
        
        // Should return class simple name when ID is null
        String expectedName = gene.getClass().getSimpleName();
        assertEquals(expectedName, gene.getId());
    }
    
    @Test
    public void testTargetIndexListSettersAndGetters() {
        Gene gene = new TestGenes.AdderGene(1.0, 0, "test");
        
        // Test setting new target index list
        gene.getTargetIndexList().clear();
        gene.getTargetIndexList().add(2);
        gene.getTargetIndexList().add(3);
        
        assertEquals(2, gene.getTargetIndexList().size());
        assertEquals(2, gene.getTargetIndexList().get(0));
        assertEquals(3, gene.getTargetIndexList().get(1));
    }
    
    @Test
    public void testOperationConstantListSettersAndGetters() {
        Gene gene = new TestGenes.AdderGene(1.0, 0, "test");
        
        // Test setting new operation constant list
        gene.getOperationConstantList().clear();
        gene.getOperationConstantList().add(2.5);
        gene.getOperationConstantList().add(3.7);
        
        assertEquals(2, gene.getOperationConstantList().size());
        assertEquals(2.5, gene.getOperationConstantList().get(0));
        assertEquals(3.7, gene.getOperationConstantList().get(1));
    }
    
    @Test
    public void testCloneProperties() {
        Gene originalGene = new TestGenes.AdderGene(5.5, 2, "original");
        originalGene.getTargetIndexList().add(3);
        originalGene.getOperationConstantList().add(7.7);
        
        Gene clonedGene = new TestGenes.AdderGene(0.0, 0, "temp");
        originalGene.cloneProperties(clonedGene);
        
        // Test that clone has different ID (UUID)
        assertNotEquals("original", clonedGene.getId());
        assertNotNull(clonedGene.getId());
        
        // Test that lists are cloned (same content, different objects)
        assertEquals(originalGene.getTargetIndexList(), clonedGene.getTargetIndexList());
        assertNotSame(originalGene.getTargetIndexList(), clonedGene.getTargetIndexList());
        
        assertEquals(originalGene.getOperationConstantList(), clonedGene.getOperationConstantList());
        assertNotSame(originalGene.getOperationConstantList(), clonedGene.getOperationConstantList());
        
        // Test that modifying original doesn't affect clone
        originalGene.getTargetIndexList().add(99);
        assertNotEquals(originalGene.getTargetIndexList().size(), clonedGene.getTargetIndexList().size());
    }
    
    @Test
    public void testCopyOf() {
        Gene originalGene = new TestGenes.AdderGene(5.5, 2, "original");
        originalGene.getOperationConstantList().add(7.7);
        
        Gene copiedGene = originalGene.copyOf();
        
        // Test that copy is a different instance
        assertNotSame(originalGene, copiedGene);
        
        // Test that copy has the same type
        assertEquals(originalGene.getClass(), copiedGene.getClass());
        
        // Test that copy has different ID
        assertNotEquals(originalGene.getId(), copiedGene.getId());
        
        // Test that copy behaves the same way
        DataQuantum originalData = new DataQuantum();
        originalData.addValue(10.0);
        originalData.addValue(20.0);
        originalData.addValue(30.0);
        
        DataQuantum copiedData = new DataQuantum();
        copiedData.addValue(10.0);
        copiedData.addValue(20.0);
        copiedData.addValue(30.0);
        
        originalGene.consume(originalData);
        copiedGene.consume(copiedData);
        
        // Results should be the same (though source IDs will be different)
        assertEquals(originalData.getValue(3), copiedData.getValue(3));
    }
    
    @Test
    public void testEquals() {
        Gene gene1 = new TestGenes.AdderGene(5.0, 1, "gene1");
        Gene gene2 = new TestGenes.AdderGene(5.0, 1, "gene2");
        Gene gene3 = new TestGenes.AdderGene(7.0, 1, "gene3");
        Gene gene4 = new TestGenes.AdderGene(5.0, 2, "gene4");
        
        // Same targetIndexList and operationConstantList should be equal (ID ignored)
        assertTrue(gene1.equals(gene2));
        
        // Different operationConstantList should not be equal
        assertFalse(gene1.equals(gene3));
        
        // Different targetIndexList should not be equal
        assertFalse(gene1.equals(gene4));
        
        // Test reflexivity
        assertTrue(gene1.equals(gene1));
        
        // Test null and different class
        assertFalse(gene1.equals(null));
        assertFalse(gene1.equals("not a gene"));
    }
    
    @Test
    public void testHashCode() {
        Gene gene1 = new TestGenes.AdderGene(5.0, 1, "gene1");
        Gene gene2 = new TestGenes.AdderGene(5.0, 1, "gene2");
        
        // Equal genes should have equal hash codes
        assertEquals(gene1.hashCode(), gene2.hashCode());
    }
    
    // Tests for Mutation behavior
    
    @Test
    public void testGetMutationCommandList() {
        Gene gene = new TestGenes.AdderGene(5.0, 1, "mutation-test");
        gene.getTargetIndexList().add(2); // Add second target index
        gene.getOperationConstantList().add(3.0); // Add second operation constant
        
        List<MutationCommand> mutations = gene.getMutationCommandList();
        
        // Should have mutations for each target index (up and down) and each operation constant (up and down)
        // 2 target indices * 2 directions + 2 operation constants * 2 directions = 8 mutations
        assertEquals(8, mutations.size());
        
        // Test that all mutations have descriptions
        for (MutationCommand mutation : mutations) {
            assertNotNull(mutation.getDescription());
            assertFalse(mutation.getDescription().trim().isEmpty());
        }
    }
    
    @Test
    public void testTargetIndexUpMutation() {
        Gene gene = new TestGenes.AdderGene(5.0, 1, "test");
        int originalValue = gene.getTargetIndexList().get(0);
        
        List<MutationCommand> mutations = gene.getMutationCommandList();
        
        // Find an "up" mutation for target index
        MutationCommand upMutation = mutations.stream()
            .filter(m -> m.getDescription().contains("Increase targetIndex[0]"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(upMutation);
        
        upMutation.execute();
        int newValue = gene.getTargetIndexList().get(0);
        
        // Value should have increased by 1-5
        assertTrue(newValue > originalValue);
        assertTrue(newValue <= originalValue + 5);
        assertTrue(newValue >= originalValue + 1);
    }
    
    @Test
    public void testTargetIndexDownMutation() {
        Gene gene = new TestGenes.AdderGene(5.0, 10, "test");
        int originalValue = gene.getTargetIndexList().get(0);
        
        List<MutationCommand> mutations = gene.getMutationCommandList();
        
        // Find a "down" mutation for target index
        MutationCommand downMutation = mutations.stream()
            .filter(m -> m.getDescription().contains("Decrease targetIndex[0]"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(downMutation);
        
        downMutation.execute();
        int newValue = gene.getTargetIndexList().get(0);
        
        // Value should have decreased by 1-5
        assertTrue(newValue < originalValue);
        assertTrue(newValue >= originalValue - 5);
        assertTrue(newValue <= originalValue - 1);
    }
    
    @Test
    public void testOperationConstantUpMutation() {
        Gene gene = new TestGenes.AdderGene(10.0, 1, "test");
        double originalValue = gene.getOperationConstantList().get(0);
        
        List<MutationCommand> mutations = gene.getMutationCommandList();
        
        // Find an "up" mutation for operation constant
        MutationCommand upMutation = mutations.stream()
            .filter(m -> m.getDescription().contains("Increase operationConstant[0]"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(upMutation);
        
        upMutation.execute();
        double newValue = gene.getOperationConstantList().get(0);
        
        // Value should have increased by 1-20%
        assertTrue(newValue > originalValue);
        assertTrue(newValue <= originalValue * 1.20);
        assertTrue(newValue >= originalValue * 1.01);
    }
    
    @Test
    public void testOperationConstantDownMutation() {
        Gene gene = new TestGenes.AdderGene(10.0, 1, "test");
        double originalValue = gene.getOperationConstantList().get(0);
        
        List<MutationCommand> mutations = gene.getMutationCommandList();
        
        // Find a "down" mutation for operation constant
        MutationCommand downMutation = mutations.stream()
            .filter(m -> m.getDescription().contains("Decrease operationConstant[0]"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(downMutation);
        
        downMutation.execute();
        double newValue = gene.getOperationConstantList().get(0);
        
        // Value should have decreased by 1-20%
        assertTrue(newValue < originalValue);
        assertTrue(newValue >= originalValue * 0.80);
        assertTrue(newValue <= originalValue * 0.99);
    }
    
    @Test
    public void testMutationCommandsAreExecutable() {
        Gene gene = new TestGenes.AdderGene(5.0, 1, "test");
        
        List<MutationCommand> mutations = gene.getMutationCommandList();
        
        // Test that all mutations can be executed without throwing exceptions
        for (MutationCommand mutation : mutations) {
            assertDoesNotThrow(() -> mutation.execute());
        }
    }
    
    @Test
    public void testMultipleTargetIndexMutations() {
        Gene gene = new TestGenes.AdderGene(5.0, 1, "test");
        gene.getTargetIndexList().add(2);
        gene.getTargetIndexList().add(3);
        
        List<MutationCommand> mutations = gene.getMutationCommandList();
        
        // Should have mutations for each target index
        long targetIndexMutations = mutations.stream()
            .filter(m -> m.getDescription().contains("targetIndex"))
            .count();
        
        // 3 target indices * 2 directions = 6 mutations
        assertEquals(6, targetIndexMutations);
    }
    
    @Test
    public void testMultipleOperationConstantMutations() {
        Gene gene = new TestGenes.AdderGene(5.0, 1, "test");
        gene.getOperationConstantList().add(7.0);
        gene.getOperationConstantList().add(9.0);
        
        List<MutationCommand> mutations = gene.getMutationCommandList();
        
        // Should have mutations for each operation constant
        long operationConstantMutations = mutations.stream()
            .filter(m -> m.getDescription().contains("operationConstant"))
            .count();
        
        // 3 operation constants * 2 directions = 6 mutations
        assertEquals(6, operationConstantMutations);
    }
}