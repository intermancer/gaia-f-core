package com.intermancer.gaiaf.core.evaluate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.intermancer.gaiaf.core.organism.Chromosome;
import com.intermancer.gaiaf.core.organism.Organism;
import com.intermancer.gaiaf.core.organism.TestGenes;

public class BasicEvaluatorTest {

    @Test
    public void testEvaluate() {
        BasicEvaluator evaluator = new BasicEvaluator();
        Organism organism = new Organism();
        Chromosome chromosome = new Chromosome();
        chromosome.getGenes().add(new TestGenes.AdderGene(1.0, -1, "adder1"));
        organism.addChromosome(chromosome);
        double score = evaluator.evaluate(organism);
        assertTrue(score > 0.0, "Score should be greater than 0.0 for a basic adder gene");
    }

}
