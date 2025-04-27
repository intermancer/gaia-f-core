package com.intermancer.gaiaf.core.experiment;

import com.intermancer.gaiaf.core.organism.Chromosome;
import com.intermancer.gaiaf.core.organism.Organism;
import com.intermancer.gaiaf.core.organism.gene.basic.AdditionGene;
import com.intermancer.gaiaf.core.organism.gene.basic.DivisionGene;
import com.intermancer.gaiaf.core.organism.gene.basic.MultiplicationGene;
import com.intermancer.gaiaf.core.organism.gene.basic.SineGene;
import com.intermancer.gaiaf.core.organism.gene.basic.SubtractionGene;
import com.intermancer.gaiaf.core.organism.repo.OrganismRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * BasicSeeder is a very basic implementation of the Seeder interface.
 * It simply and statically defines 5 Organisms and loads them into the repo.
 */
@Component
public class BasicSeeder implements Seeder {

    private final OrganismRepository organismRepository;

    @Autowired
    public BasicSeeder(OrganismRepository organismRepository) {
        this.organismRepository = organismRepository;
    }

    @Override
    public void seed() {
        createSimpleArithmeticOrganism();
        createTrigonometricAnalysisOrganism();
        createDataTransformationOrganism();
        createReductiveProcessingOrganism();
        createBasicCompositeOrganism();
    }

    /**
     * Creates a Simple Arithmetic Organism.
     * Chromosome 1: AdditionGene → MultiplicationGene
     * This organism first adds 1.5 to an input value, then multiplies by 1.5
     * Useful for demonstrating basic sequential gene operations
     */
    private void createSimpleArithmeticOrganism() {
        Organism organism = new Organism("simple-arithmetic-organism");
        Chromosome chromosome = new Chromosome();
        
        // Add AdditionGene
        AdditionGene additionGene = new AdditionGene();
        additionGene.setId("addition-gene-1");
        chromosome.getGenes().add(additionGene);
        
        // Add MultiplicationGene
        MultiplicationGene multiplicationGene = new MultiplicationGene();
        multiplicationGene.setId("multiplication-gene-1");
        chromosome.getGenes().add(multiplicationGene);
        
        organism.addChromosome(chromosome);
        organismRepository.saveOrganism(organism);
    }

    /**
     * Creates a Trigonometric Analysis Organism.
     * Chromosome 1: SineGene → MultiplicationGene
     * This organism applies sine function and then amplifies the result by 1.5
     * Demonstrates combination of trigonometric and arithmetic operations
     */
    private void createTrigonometricAnalysisOrganism() {
        Organism organism = new Organism("trigonometric-analysis-organism");
        Chromosome chromosome = new Chromosome();
        
        // Add SineGene
        SineGene sineGene = new SineGene();
        sineGene.setId("sine-gene-1");
        chromosome.getGenes().add(sineGene);
        
        // Add MultiplicationGene
        MultiplicationGene multiplicationGene = new MultiplicationGene();
        multiplicationGene.setId("multiplication-gene-2");
        chromosome.getGenes().add(multiplicationGene);
        
        organism.addChromosome(chromosome);
        organismRepository.saveOrganism(organism);
    }

    /**
     * Creates a Data Transformation Organism.
     * Chromosome 1: AdditionGene → SubtractionGene
     * Chromosome 2: MultiplicationGene → DivisionGene
     * Two parallel chromosomes: one for additive operations, another for multiplicative
     * Shows how multiple chromosomes can process data differently
     */
    private void createDataTransformationOrganism() {
        Organism organism = new Organism("data-transformation-organism");
        
        // First chromosome (additive operations)
        Chromosome chromosome1 = new Chromosome();
        AdditionGene additionGene = new AdditionGene();
        additionGene.setId("addition-gene-2");
        chromosome1.getGenes().add(additionGene);
        
        SubtractionGene subtractionGene = new SubtractionGene();
        subtractionGene.setId("subtraction-gene-1");
        chromosome1.getGenes().add(subtractionGene);
        
        // Second chromosome (multiplicative operations)
        Chromosome chromosome2 = new Chromosome();
        MultiplicationGene multiplicationGene = new MultiplicationGene();
        multiplicationGene.setId("multiplication-gene-3");
        chromosome2.getGenes().add(multiplicationGene);
        
        DivisionGene divisionGene = new DivisionGene();
        divisionGene.setId("division-gene-1");
        chromosome2.getGenes().add(divisionGene);
        
        organism.addChromosome(chromosome1);
        organism.addChromosome(chromosome2);
        organismRepository.saveOrganism(organism);
    }

    /**
     * Creates a Reductive Processing Organism.
     * Chromosome 1: DivisionGene → SubtractionGene → SineGene
     * A 3-gene chromosome that reduces values, normalizes, and applies trigonometry
     * Demonstrates longer processing chains
     */
    private void createReductiveProcessingOrganism() {
        Organism organism = new Organism("reductive-processing-organism");
        Chromosome chromosome = new Chromosome();
        
        // Add DivisionGene
        DivisionGene divisionGene = new DivisionGene();
        divisionGene.setId("division-gene-2");
        chromosome.getGenes().add(divisionGene);
        
        // Add SubtractionGene
        SubtractionGene subtractionGene = new SubtractionGene();
        subtractionGene.setId("subtraction-gene-2");
        chromosome.getGenes().add(subtractionGene);
        
        // Add SineGene
        SineGene sineGene = new SineGene();
        sineGene.setId("sine-gene-2");
        chromosome.getGenes().add(sineGene);
        
        organism.addChromosome(chromosome);
        organismRepository.saveOrganism(organism);
    }

    /**
     * Creates a Basic Composite Organism.
     * Chromosome 1: MultiplicationGene
     * Chromosome 2: AdditionGene → SineGene
     * Chromosome 3: SubtractionGene
     * Simple organism with 3 chromosomes showing different gene combinations
     * Useful for testing organism-level data flow
     */
    private void createBasicCompositeOrganism() {
        Organism organism = new Organism("basic-composite-organism");
        
        // First chromosome (single multiplication gene)
        Chromosome chromosome1 = new Chromosome();
        MultiplicationGene multiplicationGene = new MultiplicationGene();
        multiplicationGene.setId("multiplication-gene-4");
        chromosome1.getGenes().add(multiplicationGene);
        
        // Second chromosome (addition followed by sine)
        Chromosome chromosome2 = new Chromosome();
        AdditionGene additionGene = new AdditionGene();
        additionGene.setId("addition-gene-3");
        chromosome2.getGenes().add(additionGene);
        
        SineGene sineGene = new SineGene();
        sineGene.setId("sine-gene-3");
        chromosome2.getGenes().add(sineGene);
        
        // Third chromosome (single subtraction gene)
        Chromosome chromosome3 = new Chromosome();
        SubtractionGene subtractionGene = new SubtractionGene();
        subtractionGene.setId("subtraction-gene-3");
        chromosome3.getGenes().add(subtractionGene);
        
        organism.addChromosome(chromosome1);
        organism.addChromosome(chromosome2);
        organism.addChromosome(chromosome3);
        organismRepository.saveOrganism(organism);
    }
}