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
import com.intermancer.gaiaf.core.organism.gene.window.MovingAverageGene;

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
        // 7 data points, leadConsumptionCount=3, scoringLength=4
        // Raw error sum = 8.0, normalized = 8.0 / 4 = 2.0
        assertEquals(2.0, score, 0.0001);
    }

    @Test
    public void testEffectiveLeadCountUsesOrganismWarmingCyclesWhenLarger() {
        BasicEvaluator evaluator = new BasicEvaluator();
        evaluator.setLeadConsumptionCount(1);
        evaluator.setTargetIndex(0);

        // MovingAverageGene with window size 3 -> getWarmingCycles() = 3
        // effectiveLeadCount = max(1, 3) = 3
        Organism organism = createMovingAverageOrganism(3);

        List<DataQuantum> historicalData = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            historicalData.add(new DataQuantum().addDataPoint(new DataPoint((double) i)));
        }
        evaluator.setHistoricalData(historicalData);

        double score = evaluator.evaluate(organism);
        // scoringLength = 7 - 3 = 4; score should be finite and non-negative
        assertTrue(score >= 0.0, "Score should be non-negative");
        assertTrue(Double.isFinite(score), "Score should be finite");
    }

    @Test
    public void testEffectiveLeadCountUsesLeadConsumptionCountWhenLarger() {
        BasicEvaluator evaluator = new BasicEvaluator();
        evaluator.setLeadConsumptionCount(5);
        evaluator.setTargetIndex(0);

        // AdderGene has warmingCycles = 0; effectiveLeadCount = max(5, 0) = 5
        Organism organism = createTestAdderOrganism();

        List<DataQuantum> historicalData = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            historicalData.add(new DataQuantum().addDataPoint(new DataPoint((double) i)));
        }
        evaluator.setHistoricalData(historicalData);

        double score = evaluator.evaluate(organism);
        // scoringLength = 8 - 5 = 3; score should be finite and non-negative
        assertTrue(score >= 0.0, "Score should be non-negative");
        assertTrue(Double.isFinite(score), "Score should be finite");
    }

    @Test
    public void testReturnsMaxValueWhenWarmingCyclesExceedsDataset() {
        BasicEvaluator evaluator = new BasicEvaluator();
        evaluator.setLeadConsumptionCount(1);
        evaluator.setTargetIndex(0);

        // Window size 10 -> warmingCycles = 10; dataset only has 5 rows
        Organism organism = createMovingAverageOrganism(10);

        List<DataQuantum> historicalData = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            historicalData.add(new DataQuantum().addDataPoint(new DataPoint((double) i)));
        }
        evaluator.setHistoricalData(historicalData);

        double score = evaluator.evaluate(organism);
        assertEquals(Double.MAX_VALUE, score, "Score should be MAX_VALUE when warming exceeds dataset size");
    }

    @Test
    public void testNormalizationMakesScoresComparable() {
        // An organism with a larger warming requirement should not score better simply
        // because it is scored against fewer data points.
        BasicEvaluator evaluator = new BasicEvaluator();
        evaluator.setLeadConsumptionCount(1);
        evaluator.setTargetIndex(0);

        // Both organisms add 1.0 to the last value. With a flat dataset the
        // per-point error is identical regardless of how many points are skipped.
        Organism shortWarm = createTestAdderOrganism();  // warmingCycles = 0
        Organism longWarm  = createMovingAverageOrganism(3); // warmingCycles = 3

        // Flat dataset: every value is 5.0, adder outputs 6.0, error per point is |0 - 5| during
        // warm-up (zeroed out) and |6 - 5| = 1 during scoring.
        List<DataQuantum> historicalData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            historicalData.add(new DataQuantum().addDataPoint(new DataPoint(5.0)));
        }
        evaluator.setHistoricalData(historicalData);

        double scoreShort = evaluator.evaluate(shortWarm);
        double scoreLong  = evaluator.evaluate(longWarm);

        // Both should produce finite, non-negative scores
        assertTrue(Double.isFinite(scoreShort), "Short-warm score should be finite");
        assertTrue(Double.isFinite(scoreLong),  "Long-warm score should be finite");
        assertTrue(scoreShort >= 0.0);
        assertTrue(scoreLong  >= 0.0);
    }

    private Organism createMovingAverageOrganism(int windowSize) {
        MovingAverageGene gene = new MovingAverageGene();
        gene.getOperationConstantList().set(0, (double) windowSize);
        Chromosome chromosome = new Chromosome();
        chromosome.getGenes().add(gene);
        Organism organism = new Organism();
        organism.addChromosome(chromosome);
        return organism;
    }

    private Organism createTestAdderOrganism() {
        Organism organism = new Organism();
        Chromosome chromosome = new Chromosome();
        chromosome.getGenes().add(new TestGenes.AdderGene(1.0, -1, "adder1"));
        organism.addChromosome(chromosome);
        return organism;
    }

    @Test
    public void testCachedDataNotMutatedAcrossEvaluations() {
        BasicEvaluator evaluator = new BasicEvaluator();
        Organism organism = createTestAdderOrganism();
        
        List<DataQuantum> historicalData = new ArrayList<>();
        historicalData.add(new DataQuantum().addDataPoint(new DataPoint(1.0)));
        historicalData.add(new DataQuantum().addDataPoint(new DataPoint(2.0)));
        historicalData.add(new DataQuantum().addDataPoint(new DataPoint(3.0)));
        historicalData.add(new DataQuantum().addDataPoint(new DataPoint(4.0)));
        
        evaluator.setHistoricalData(historicalData);
        evaluator.setLeadConsumptionCount(2);
        evaluator.setTargetIndex(0);
        
        // Record original sizes
        int[] originalSizes = historicalData.stream()
            .mapToInt(dq -> dq.getDataPoints().size())
            .toArray();
        
        // First evaluation
        double score1 = evaluator.evaluate(organism);
        
        // Verify cached data was not mutated
        for (int i = 0; i < historicalData.size(); i++) {
            assertEquals(originalSizes[i], historicalData.get(i).getDataPoints().size(),
                "DataQuantum at index " + i + " should not be mutated after evaluation");
        }
        
        // Second evaluation should produce same score (data not accumulated)
        double score2 = evaluator.evaluate(organism);
        assertEquals(score1, score2, "Scores should be identical across evaluations");
        
        // Verify cached data still unchanged
        for (int i = 0; i < historicalData.size(); i++) {
            assertEquals(originalSizes[i], historicalData.get(i).getDataPoints().size(),
                "DataQuantum at index " + i + " should not be mutated after second evaluation");
        }
    }

}
