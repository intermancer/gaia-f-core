package com.intermancer.gaiaf.core.organism.breeding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.intermancer.gaiaf.core.organism.Chromosome;
import com.intermancer.gaiaf.core.organism.Organism;
import com.intermancer.gaiaf.core.organism.TestGenes;

public class BasicOrganismBreederTest {

    @Test
    public void testBreedWithEmptyParentsList() {
        BasicOrganismBreeder breeder = new BasicOrganismBreeder();
        List<Organism> parents = new ArrayList<>();
        
        List<Organism> children = breeder.breed(parents);
        
        assertTrue(children.isEmpty(), "Breeding with no parents should return an empty list");
    }
    
    @Test
    public void testBreedWithNullParentsList() {
        BasicOrganismBreeder breeder = new BasicOrganismBreeder();
        
        List<Organism> children = breeder.breed(null);
        
        assertTrue(children.isEmpty(), "Breeding with null parents should return an empty list");
    }
    
    @Test
    public void testBreedWithSingleParent() {
        BasicOrganismBreeder breeder = new BasicOrganismBreeder();
        
        // Create a parent organism with 2 chromosomes
        Organism parent = new Organism("parent-1");
        
        Chromosome chromosome1 = new Chromosome();
        chromosome1.getGenes().add(new TestGenes.AdderGene(5.0, 0, "add5-gene"));
        
        Chromosome chromosome2 = new Chromosome();
        chromosome2.getGenes().add(new TestGenes.MultiplierGene(2.0, 0, "double-gene"));
        
        parent.addChromosome(chromosome1);
        parent.addChromosome(chromosome2);
        
        List<Organism> parents = Arrays.asList(parent);
        List<Organism> children = breeder.breed(parents);
        
        // Verify results
        assertEquals(1, children.size(), "Should create one child from one parent");
        
        Organism child = children.get(0);
        assertNotNull(child.getId(), "Child should have an ID");
        assertNotEquals("parent-1", child.getId(), "Child should have a different ID than parent");
        
        assertEquals(2, child.getChromosomes().size(), "Child should have the same number of chromosomes as parent");
        
        // Verify the child's chromosomes are the same as the parent's
        assertEquals(parent.getChromosomes().get(0), child.getChromosomes().get(0), 
                "First chromosome should be the same");
        assertEquals(parent.getChromosomes().get(1), child.getChromosomes().get(1), 
                "Second chromosome should be the same");
    }
    
    @Test
    public void testBreedWithMultipleParents() {
        BasicOrganismBreeder breeder = new BasicOrganismBreeder();
        
        // Create parent organisms
        Organism parent1 = new Organism("parent-1");
        Chromosome p1c1 = new Chromosome();
        p1c1.getGenes().add(new TestGenes.AdderGene(1.0, 0, "p1-add-gene"));
        parent1.addChromosome(p1c1);
        
        Organism parent2 = new Organism("parent-2");
        Chromosome p2c1 = new Chromosome();
        p2c1.getGenes().add(new TestGenes.MultiplierGene(2.0, 0, "p2-mult-gene"));
        parent2.addChromosome(p2c1);
        
        Organism parent3 = new Organism("parent-3");
        Chromosome p3c1 = new Chromosome();
        p3c1.getGenes().add(new TestGenes.SquareGene(0, "p3-square-gene"));
        parent3.addChromosome(p3c1);
        
        List<Organism> parents = Arrays.asList(parent1, parent2, parent3);
        List<Organism> children = breeder.breed(parents);
        
        // Verify results
        assertEquals(3, children.size(), "Should create three children from three parents");
        
        // Verify child 1 (should have chromosomes from parent1)
        Organism child1 = children.get(0);
        assertEquals(1, child1.getChromosomes().size(), "Child 1 should have one chromosome");
        assertSame(p1c1, child1.getChromosomes().get(0), 
                "Child 1's chromosome should be from parent 1");
        
        // Verify child 2 (should have chromosomes from parent2)
        Organism child2 = children.get(1);
        assertEquals(1, child2.getChromosomes().size(), "Child 2 should have one chromosome");
        assertSame(p2c1, child2.getChromosomes().get(0), 
                "Child 2's chromosome should be from parent 2");
        
        // Verify child 3 (should have chromosomes from parent3)
        Organism child3 = children.get(2);
        assertEquals(1, child3.getChromosomes().size(), "Child 3 should have one chromosome");
        assertSame(p3c1, child3.getChromosomes().get(0), 
                "Child 3's chromosome should be from parent 3");
    }
    
    @Test
    public void testBreedWithDifferentChromosomeCounts() {
        BasicOrganismBreeder breeder = new BasicOrganismBreeder();
        
        // Create parent organisms with different numbers of chromosomes
        Organism parent1 = new Organism("parent-1");
        Chromosome p1c1 = new Chromosome();
        p1c1.getGenes().add(new TestGenes.AdderGene(1.0, 0, "p1-add-gene"));
        
        Chromosome p1c2 = new Chromosome();
        p1c2.getGenes().add(new TestGenes.MultiplierGene(2.0, 0, "p1-mult-gene"));
        
        parent1.addChromosome(p1c1);
        parent1.addChromosome(p1c2);
        
        Organism parent2 = new Organism("parent-2");
        Chromosome p2c1 = new Chromosome();
        p2c1.getGenes().add(new TestGenes.SquareGene(0, "p2-square-gene"));
        parent2.addChromosome(p2c1);
        
        List<Organism> parents = Arrays.asList(parent1, parent2);
        List<Organism> children = breeder.breed(parents);
        
        // Verify results
        assertEquals(2, children.size(), "Should create two children from two parents");
        
        // Verify child 1
        Organism child1 = children.get(0);
        assertEquals(1, child1.getChromosomes().size(), "Child 1 should have two chromosomes");
        assertEquals(p1c1, child1.getChromosomes().get(0), 
                "Child 1's first chromosome should be from parent 1");
        
        // Verify child 2
        Organism child2 = children.get(1);
        assertEquals(2, child2.getChromosomes().size(), "Child 2 should have two chromosomes");
        assertSame(p2c1, child2.getChromosomes().get(0), 
                "Child 2's first chromosome should be from parent 2");
        assertSame(p1c2, child2.getChromosomes().get(1), 
                "Child 2's second chromosome should be from parent 1");
    }
    
    @Test
    public void testChromosomeWrappingInBreeding() {
        BasicOrganismBreeder breeder = new BasicOrganismBreeder();
        
        // Create 3 parents with multiple chromosomes
        Organism parent1 = new Organism("parent-1");
        Chromosome p1c1 = new Chromosome();
        Chromosome p1c2 = new Chromosome();
        parent1.addChromosome(p1c1);
        parent1.addChromosome(p1c2);
        
        Organism parent2 = new Organism("parent-2");
        Chromosome p2c1 = new Chromosome();
        parent2.addChromosome(p2c1);
        
        Organism parent3 = new Organism("parent-3");
        Chromosome p3c1 = new Chromosome();
        Chromosome p3c2 = new Chromosome();
        Chromosome p3c3 = new Chromosome();
        parent3.addChromosome(p3c1);
        parent3.addChromosome(p3c2);
        parent3.addChromosome(p3c3);
        
        List<Organism> parents = Arrays.asList(parent1, parent2, parent3);
        List<Organism> children = breeder.breed(parents);
        
        assertEquals(3, children.size());
        
        // Child 1 should have: p1c1, p2c1, p3c1
        Organism child1 = children.get(0);
        assertEquals(2, child1.getChromosomes().size());
        assertEquals(p1c1, child1.getChromosomes().get(0));
        assertEquals(p3c3, child1.getChromosomes().get(1));
        
        // Child 2 should have: p2c1, p3c1, p1c1
        Organism child2 = children.get(1);
        assertEquals(2, child2.getChromosomes().size());
        assertEquals(p2c1, child2.getChromosomes().get(0));
        assertEquals(p3c2, child2.getChromosomes().get(1));
        
        // Child 3 should have: p3c1, p1c1, p2c1
        Organism child3 = children.get(2);
        assertEquals(2, child3.getChromosomes().size());
        assertEquals(p3c1, child3.getChromosomes().get(0));
        assertEquals(p1c2, child3.getChromosomes().get(1));
    }
    
    @Test
    public void testGeneratedChildrenHaveUniqueIds() {
        BasicOrganismBreeder breeder = new BasicOrganismBreeder();
        
        // Create parent organisms
        Organism parent1 = new Organism("parent-1");
        Chromosome chromosome1 = new Chromosome();
        parent1.addChromosome(chromosome1);
        
        Organism parent2 = new Organism("parent-2");
        Chromosome chromosome2 = new Chromosome();
        parent2.addChromosome(chromosome2);
        
        List<Organism> parents = Arrays.asList(parent1, parent2);
        List<Organism> children = breeder.breed(parents);
        
        // Verify all children have unique IDs
        assertEquals(2, children.size());
        assertNotEquals(children.get(0).getId(), children.get(1).getId(), 
                "Children should have unique IDs");
        
        // Verify children's IDs are not the same as parents'
        assertNotEquals("parent-1", children.get(0).getId());
        assertNotEquals("parent-2", children.get(0).getId());
        assertNotEquals("parent-1", children.get(1).getId());
        assertNotEquals("parent-2", children.get(1).getId());
    }
}