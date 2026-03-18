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
import com.intermancer.gaiaf.core.organism.gene.basic.DataPointAdditionGene;
import com.intermancer.gaiaf.core.organism.gene.basic.DataPointDivisionGene;
import com.intermancer.gaiaf.core.organism.gene.basic.DataPointMultiplicationGene;
import com.intermancer.gaiaf.core.organism.gene.basic.DataPointSubtractionGene;
import com.intermancer.gaiaf.core.organism.gene.basic.DivisionGene;
import com.intermancer.gaiaf.core.organism.gene.basic.MultiplicationGene;
import com.intermancer.gaiaf.core.organism.gene.basic.SineGene;
import com.intermancer.gaiaf.core.organism.gene.basic.SubtractionGene;
import com.intermancer.gaiaf.core.organism.gene.control.ClampGene;
import com.intermancer.gaiaf.core.organism.gene.control.SelectMaxGene;
import com.intermancer.gaiaf.core.organism.gene.control.SelectMinGene;
import com.intermancer.gaiaf.core.organism.gene.control.ThresholdSwitchGene;
import com.intermancer.gaiaf.core.organism.gene.window.DelayGene;
import com.intermancer.gaiaf.core.organism.gene.window.MovingAverageGene;

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
        
        // Should generate representatives from all three Gene categories
        assertTrue(geneTypes.contains(AdditionGene.class), "Should generate AdditionGene");
        assertTrue(geneTypes.contains(SubtractionGene.class), "Should generate SubtractionGene");
        assertTrue(geneTypes.contains(MultiplicationGene.class), "Should generate MultiplicationGene");
        assertTrue(geneTypes.contains(DivisionGene.class), "Should generate DivisionGene");
        assertTrue(geneTypes.contains(SineGene.class), "Should generate SineGene");

        // Should generate the pool from all 26 gene types with enough samples
        assertTrue(geneTypes.size() > 5, "Should generate more than just the 5 basic gene types");
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

            // The last target index is always -1 (chain convention)
            assertEquals(-1, targetIndexList.get(targetIndexList.size() - 1),
                "Last target index should always be -1");

            // All indices should be negative (relative indexing)
            for (Integer index : targetIndexList) {
                assertTrue(index < 0, "Target indices should be negative, found: " + index);
            }
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
        Set<Class<?>> observedTypes = new HashSet<>();

        // Generate enough genes to expect all 26 types to appear
        for (int i = 0; i < 2000; i++) {
            observedTypes.add(GeneGenerator.getRandomGene().getClass());
        }

        // Spot-check representatives from each category
        assertTrue(observedTypes.contains(AdditionGene.class), "Basic: AdditionGene missing");
        assertTrue(observedTypes.contains(DataPointAdditionGene.class), "Basic multi: DataPointAdditionGene missing");
        assertTrue(observedTypes.contains(DataPointSubtractionGene.class), "Basic multi: DataPointSubtractionGene missing");
        assertTrue(observedTypes.contains(DataPointMultiplicationGene.class), "Basic multi: DataPointMultiplicationGene missing");
        assertTrue(observedTypes.contains(DataPointDivisionGene.class), "Basic multi: DataPointDivisionGene missing");
        assertTrue(observedTypes.contains(MovingAverageGene.class), "Window: MovingAverageGene missing");
        assertTrue(observedTypes.contains(DelayGene.class), "Window: DelayGene missing");
        assertTrue(observedTypes.contains(ClampGene.class), "Control: ClampGene missing");
        assertTrue(observedTypes.contains(ThresholdSwitchGene.class), "Control: ThresholdSwitchGene missing");
        assertTrue(observedTypes.contains(SelectMaxGene.class), "Control: SelectMaxGene missing");
        assertTrue(observedTypes.contains(SelectMinGene.class), "Control: SelectMinGene missing");

        // All 27 types should appear with 2000 samples
        assertEquals(27, observedTypes.size(), "Expected all 27 gene types to appear");
    }
}