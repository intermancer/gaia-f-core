package com.intermancer.gaiaf.core.experiment;

import com.intermancer.gaiaf.core.evaluate.Evaluator;
import com.intermancer.gaiaf.core.evaluate.ScoredOrganism;
import com.intermancer.gaiaf.core.evaluate.ScoredOrganismRepository;
import com.intermancer.gaiaf.core.organism.Chromosome;
import com.intermancer.gaiaf.core.organism.Organism;
import com.intermancer.gaiaf.core.organism.gene.basic.*;
import com.intermancer.gaiaf.core.organism.repo.OrganismRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * BasicSeeder is a very basic implementation of the Seeder interface.
 * It statically defines 5 Organisms, evaluates them, and loads them into the repositories.
 */
@Component
public class BasicSeeder implements Seeder {

    private final ScoredOrganismRepository scoredOrganismRepository;
    private final OrganismRepository organismRepository;
    private final Evaluator evaluator;

    @Autowired
    public BasicSeeder(ScoredOrganismRepository scoredOrganismRepository,
                      OrganismRepository organismRepository,
                      Evaluator evaluator) {
        this.scoredOrganismRepository = scoredOrganismRepository;
        this.organismRepository = organismRepository;
        this.evaluator = evaluator;
    }

    @Override
    public void seed(String experimentId) {
        createSimpleArithmeticOrganism(experimentId);
        createTrigonometricAnalysisOrganism(experimentId);
        createDataTransformationOrganism(experimentId);
        createReductiveProcessingOrganism(experimentId);
        createBasicCompositeOrganism(experimentId);
    }

    /**
     * Creates a Simple Arithmetic Organism.
     * Chromosome 1: AdditionGene → MultiplicationGene
     * This organism first adds 1.5 to an input value, then multiplies by 1.5
     * Useful for demonstrating basic sequential gene operations
     */
    private void createSimpleArithmeticOrganism(String experimentId) {
        Organism organism = new Organism(UUID.randomUUID().toString());
        Chromosome chromosome = new Chromosome();
        
        AdditionGene additionGene = new AdditionGene();
        additionGene.setId(UUID.randomUUID().toString());
        chromosome.getGenes().add(additionGene);
        
        MultiplicationGene multiplicationGene = new MultiplicationGene();
        multiplicationGene.setId(UUID.randomUUID().toString());
        chromosome.getGenes().add(multiplicationGene);
        
        organism.addChromosome(chromosome);
        saveAndScoreOrganism(organism, experimentId);
    }

    /**
     * Creates a Trigonometric Analysis Organism.
     * Chromosome 1: SineGene → MultiplicationGene
     * This organism applies sine function and then amplifies the result by 1.5
     * Demonstrates combination of trigonometric and arithmetic operations
     */
    private void createTrigonometricAnalysisOrganism(String experimentId) {
        Organism organism = new Organism(UUID.randomUUID().toString());
        Chromosome chromosome = new Chromosome();
        
        SineGene sineGene = new SineGene();
        sineGene.setId(UUID.randomUUID().toString());
        chromosome.getGenes().add(sineGene);
        
        MultiplicationGene multiplicationGene = new MultiplicationGene();
        multiplicationGene.setId(UUID.randomUUID().toString());
        chromosome.getGenes().add(multiplicationGene);
        
        organism.addChromosome(chromosome);
        saveAndScoreOrganism(organism, experimentId);
    }

    /**
     * Creates a Data Transformation Organism.
     * Chromosome 1: AdditionGene → SubtractionGene
     * Chromosome 2: MultiplicationGene → DivisionGene
     * Two parallel chromosomes: one for additive operations, another for multiplicative
     * Shows how multiple chromosomes can process data differently
     */
    private void createDataTransformationOrganism(String experimentId) {
        Organism organism = new Organism(UUID.randomUUID().toString());
        
        Chromosome chromosome1 = new Chromosome();
        AdditionGene additionGene = new AdditionGene();
        additionGene.setId(UUID.randomUUID().toString());
        chromosome1.getGenes().add(additionGene);
        
        SubtractionGene subtractionGene = new SubtractionGene();
        subtractionGene.setId(UUID.randomUUID().toString());
        chromosome1.getGenes().add(subtractionGene);
        
        Chromosome chromosome2 = new Chromosome();
        MultiplicationGene multiplicationGene = new MultiplicationGene();
        multiplicationGene.setId(UUID.randomUUID().toString());
        chromosome2.getGenes().add(multiplicationGene);
        
        DivisionGene divisionGene = new DivisionGene();
        divisionGene.setId(UUID.randomUUID().toString());
        chromosome2.getGenes().add(divisionGene);
        
        organism.addChromosome(chromosome1);
        organism.addChromosome(chromosome2);
        saveAndScoreOrganism(organism, experimentId);
    }

    /**
     * Creates a Reductive Processing Organism.
     * Chromosome 1: DivisionGene → SubtractionGene → SineGene
     * A 3-gene chromosome that reduces values, normalizes, and applies trigonometry
     * Demonstrates longer processing chains
     */
    private void createReductiveProcessingOrganism(String experimentId) {
        Organism organism = new Organism(UUID.randomUUID().toString());
        Chromosome chromosome = new Chromosome();
        
        DivisionGene divisionGene = new DivisionGene();
        divisionGene.setId(UUID.randomUUID().toString());
        chromosome.getGenes().add(divisionGene);
        
        SubtractionGene subtractionGene = new SubtractionGene();
        subtractionGene.setId(UUID.randomUUID().toString());
        chromosome.getGenes().add(subtractionGene);
        
        SineGene sineGene = new SineGene();
        sineGene.setId(UUID.randomUUID().toString());
        chromosome.getGenes().add(sineGene);
        
        organism.addChromosome(chromosome);
        saveAndScoreOrganism(organism, experimentId);
    }

    /**
     * Creates a Basic Composite Organism.
     * Chromosome 1: MultiplicationGene
     * Chromosome 2: AdditionGene → SineGene
     * Chromosome 3: SubtractionGene
     * Simple organism with 3 chromosomes showing different gene combinations
     * Useful for testing organism-level data flow
     */
    private void createBasicCompositeOrganism(String experimentId) {
        Organism organism = new Organism(UUID.randomUUID().toString());
        
        Chromosome chromosome1 = new Chromosome();
        MultiplicationGene multiplicationGene = new MultiplicationGene();
        multiplicationGene.setId(UUID.randomUUID().toString());
        chromosome1.getGenes().add(multiplicationGene);
        
        Chromosome chromosome2 = new Chromosome();
        AdditionGene additionGene = new AdditionGene();
        additionGene.setId(UUID.randomUUID().toString());
        chromosome2.getGenes().add(additionGene);
        
        SineGene sineGene = new SineGene();
        sineGene.setId(UUID.randomUUID().toString());
        chromosome2.getGenes().add(sineGene);
        
        Chromosome chromosome3 = new Chromosome();
        SubtractionGene subtractionGene = new SubtractionGene();
        subtractionGene.setId(UUID.randomUUID().toString());
        chromosome3.getGenes().add(subtractionGene);
        
        organism.addChromosome(chromosome1);
        organism.addChromosome(chromosome2);
        organism.addChromosome(chromosome3);
        saveAndScoreOrganism(organism, experimentId);
    }

    /**
     * Helper method to save an organism and its evaluated score to the repositories.
     * 
     * @param organism The organism to save and score
     */
    private void saveAndScoreOrganism(Organism organism, String experimentId) {
        // Save the organism to the OrganismRepository
        Organism savedOrganism = organismRepository.saveOrganism(organism);
        
        // Evaluate the organism
        double score = evaluator.evaluate(savedOrganism);
        
        // Create and save a ScoredOrganism
        ScoredOrganism scoredOrganism = new ScoredOrganism(
                null, 
                score, 
                savedOrganism.getId(), 
                savedOrganism,
                experimentId
        );
        scoredOrganismRepository.save(scoredOrganism);
    }
}