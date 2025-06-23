package com.intermancer.gaiaf.core.experiment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import com.intermancer.gaiaf.core.organism.Gene;
import com.intermancer.gaiaf.core.organism.gene.basic.AdditionGene;
import com.intermancer.gaiaf.core.organism.gene.basic.DivisionGene;
import com.intermancer.gaiaf.core.organism.gene.basic.MultiplicationGene;
import com.intermancer.gaiaf.core.organism.gene.basic.SineGene;
import com.intermancer.gaiaf.core.organism.gene.basic.SubtractionGene;

public class GeneGeneratorTest {
    
    @Test
    public void testGetRandomGeneReturnsValidGene() {
        Gene gene = GeneGenerator.getRandomGene();
        
        assertNotNull(gene);
        assertNotNull(gene.getId());
        assertNotNull(gene.getTargetIndexList());
        assertNotNull(gene.getOperationConstantList());
        
        // Gene should have at least one target index
        assertFalse(gene.getTargetIndexList().isEmpty());
    }
    
    @Test
    public void testGetRandomGeneReturnsBasicGeneTypes() {
        Set<Class<?>> geneTypes = new HashSet<>();
        
        // Generate multiple genes and check for variety in types
        for (int i = 0; i < 100; i++) {
            Gene gene = GeneGenerator.getRandomGene();
            geneTypes.add(gene.getClass());
        }
        
        // Should generate the basic gene types
        assertTrue(geneTypes.contains(AdditionGene.class));
        assertTrue(geneTypes.contains(SubtractionGene.class));
        assertTrue(geneTypes.contains(MultiplicationGene.class));
        assertTrue(geneTypes.contains(DivisionGene.class));
        assertTrue(geneTypes.contains(SineGene.class));
        
        // Should generate all 5 types
        assertEquals(5, geneTypes.size(), "Should generate all 5 basic gene types");
    }
    
    @Test
    public void testGetRandomGeneHasUniqueIds() {
        Set<String> generatedIds = new HashSet<>();
        
        // Generate multiple genes and verify they have unique IDs
        for (int i = 0; i < 100; i++) {
            Gene gene = GeneGenerator.getRandomGene();
            String id = gene.getId();
            
            assertNotNull(id);
            assertFalse(id.trim().isEmpty());
            assertTrue(generatedIds.add(id), "Generated gene ID should be unique: " + id);
        }
    }
    
    @Test
    public void testGetRandomGeneIdFormat() {
        for (int i = 0; i < 50; i++) {
            Gene gene = GeneGenerator.getRandomGene();
            String id = gene.getId();
            
            // ID should contain the class name and a UUID portion
            assertTrue(id.contains(gene.getClass().getSimpleName()), 
                      "ID should contain class name: " + id);
            assertTrue(id.contains("-"), "ID should contain UUID separator: " + id);
            
            // Should be in format: ClassName-UUID
            String[] parts = id.split("-");
            assertTrue(parts.length >= 2, "ID should have at least class name and UUID part");
            assertEquals(gene.getClass().getSimpleName(), parts[0], 
                        "First part should be class name");
        }
    }
    
    @Test
    public void testGetRandomGeneTargetIndexList() {
        for (int i = 0; i < 50; i++) {
            Gene gene = GeneGenerator.getRandomGene();
            List<Integer> targetIndexList = gene.getTargetIndexList();
            
            assertNotNull(targetIndexList);
            assertFalse(targetIndexList.isEmpty());
            
            // According to the code, basic genes use default -1 for single target index
            assertEquals(1, targetIndexList.size(), "Basic genes should have one target index");
            assertEquals(-1, targetIndexList.get(0), "Default target index should be -1");
        }
    }
    
    @Test
    public void testGetRandomGeneConstantRandomization() {
        Set<Double> constants = new HashSet<>();
        
        // Generate many arithmetic genes and collect their constants
        for (int i = 0; i < 200; i++) {
            Gene gene = GeneGenerator.getRandomGene();
            
            if (!gene.getOperationConstantList().isEmpty()) {
                constants.add(gene.getOperationConstantList().get(0));
            }
        }
        
        // Should have variety in generated constants
        assertTrue(constants.size() > 10, "Should generate variety of constants, found: " + constants.size());
        
        // Verify range
        for (Double constant : constants) {
            assertTrue(constant >= 0.1 && constant <= 10.0, 
                      "All constants should be in range 0.1-10.0, found: " + constant);
        }
    }
    
    @Test
    public void testGetRandomGeneFunctionality() {
        for (int i = 0; i < 20; i++) {
            Gene gene = GeneGenerator.getRandomGene();
            
            // Create test data
            DataQuantum dataQuantum = new DataQuantum();
            dataQuantum.addValue(10.0);
            dataQuantum.addValue(20.0);
            dataQuantum.addValue(30.0);
            
            int originalSize = dataQuantum.getDataPoints().size();
            
            // Gene should process the data
            gene.consume(dataQuantum);
            
            // Should have added at least one new data point
            assertTrue(dataQuantum.getDataPoints().size() > originalSize, 
                "Gene should add data points when consuming");
            
            // New data points should have finite values
            for (int j = originalSize; j < dataQuantum.getDataPoints().size(); j++) {
                double value = dataQuantum.getDataPoint(j).getValue();
                assertTrue(Double.isFinite(value), "Generated value should be finite: " + value);
            }
        }
    }
    
    @Test
    public void testGetRandomGeneDistribution() {
        int[] counts = new int[5]; // For 5 gene types
        
        // Generate many genes and count distribution
        for (int i = 0; i < 500; i++) {
            Gene gene = GeneGenerator.getRandomGene();
            
            if (gene instanceof AdditionGene) counts[0]++;
            else if (gene instanceof SubtractionGene) counts[1]++;
            else if (gene instanceof MultiplicationGene) counts[2]++;
            else if (gene instanceof DivisionGene) counts[3]++;
            else if (gene instanceof SineGene) counts[4]++;
        }
        
        // Each type should appear at least once in 500 generations
        for (int i = 0; i < 5; i++) {
            assertTrue(counts[i] > 0, "Gene type " + i + " should appear at least once");
        }
        
        // Distribution should be roughly even (allowing for randomness)
        for (int i = 0; i < 5; i++) {
            assertTrue(counts[i] > 50, "Gene type " + i + " should appear reasonably often, was: " + counts[i]);
            assertTrue(counts[i] < 200, "Gene type " + i + " should not dominate, was: " + counts[i]);
        }
    }
}