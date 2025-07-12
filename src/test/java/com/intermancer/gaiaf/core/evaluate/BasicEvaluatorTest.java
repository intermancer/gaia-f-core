package com.intermancer.gaiaf.core.evaluate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.intermancer.gaiaf.core.organism.Chromosome;
import com.intermancer.gaiaf.core.organism.DataQuantum;
import com.intermancer.gaiaf.core.organism.DataQuantum.DataPoint;
import com.intermancer.gaiaf.core.organism.Organism;
import com.intermancer.gaiaf.core.organism.TestGenes;

public class BasicEvaluatorTest {

    /**
     * Test to ensure that the BasicEvaluator can evaluate an organism with a basic adder gene.
     * This test checks that the default training data is loaded correctly and that the evaluation
     * process does not throw any exceptions.
     */
    @Test
    public void testFullEvaluationCycle() {
        BasicEvaluator evaluator = new BasicEvaluator();
        Organism organism = createTestAdderOrganism();
        double score = evaluator.evaluate(organism);
        assertTrue(score > 0.0, "Score should be greater than 0.0 for a basic adder gene");
    }

    @Test
    public void testHappyPathEvaluation() {
        BasicEvaluator evaluator = new BasicEvaluator();
        Organism organism = createTestAdderOrganism();
        
        List<DataQuantum> historicalData = new ArrayList<>();
        historicalData.add(new DataQuantum().addDataPoint(new DataPoint(1.0)));
        historicalData.add(new DataQuantum().addDataPoint(new DataPoint(2.0)));
        historicalData.add(new DataQuantum().addDataPoint(new DataPoint(3.0)));
        historicalData.add(new DataQuantum().addDataPoint(new DataPoint(4.0)));
        historicalData.add(new DataQuantum().addDataPoint(new DataPoint(5.0)));
        historicalData.add(new DataQuantum().addDataPoint(new DataPoint(6.0)));
        historicalData.add(new DataQuantum().addDataPoint(new DataPoint(7.0)));
        
        evaluator.setHistoricalData(historicalData);
        evaluator.setLeadConsumptionCount(3);
        evaluator.setTargetIndex(0); // Assuming the target index is 0 for this test

        double score = evaluator.evaluate(organism);
        assertEquals(8.0, score);
    }

    private Organism createTestAdderOrganism() {
        Organism organism = new Organism();
        Chromosome chromosome = new Chromosome();
        chromosome.getGenes().add(new TestGenes.AdderGene(1.0, -1, "adder1"));
        organism.addChromosome(chromosome);
        return organism;
    }

}
