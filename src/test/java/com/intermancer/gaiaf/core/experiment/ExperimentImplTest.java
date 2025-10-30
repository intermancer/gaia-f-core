package com.intermancer.gaiaf.core.experiment;

import com.intermancer.gaiaf.core.evaluate.Evaluator;
import com.intermancer.gaiaf.core.evaluate.ScoredOrganism;
import com.intermancer.gaiaf.core.evaluate.ScoredOrganismRepository;
import com.intermancer.gaiaf.core.organism.Organism;
import com.intermancer.gaiaf.core.organism.breeding.OrganismBreeder;
import com.intermancer.gaiaf.core.organism.repo.OrganismRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.*;

/**
 * Test class for ExperimentImpl.
 */
@ExtendWith(MockitoExtension.class)
class ExperimentImplTest {

    @Mock
    private Seeder seeder;

    @Mock
    private OrganismRepository organismRepository;

    @Mock
    private ScoredOrganismRepository scoredOrganismRepository;

    @Mock
    private OrganismBreeder organismBreeder;

    @Mock
    private Evaluator evaluator;

    private ExperimentImpl experiment;

    @BeforeEach
    void setUp() {
        experiment = new ExperimentImpl(
                seeder,
                organismRepository,
                scoredOrganismRepository,
                organismBreeder,
                evaluator
        );
    }

    @Test
    void testSeed() {
        // When
        experiment.seed();

        // Then
        verify(seeder, times(1)).seed(organismRepository);
    }

    @Test
    void testSelectParents() {
        // Given
        Organism parent1 = new Organism("parent1");
        Organism parent2 = new Organism("parent2");
        ScoredOrganism scoredParent1 = new ScoredOrganism("sp1", 1.0, "parent1", parent1);
        ScoredOrganism scoredParent2 = new ScoredOrganism("sp2", 5.0, "parent2", parent2);

        when(scoredOrganismRepository.getRandomFromTopPercent(0.1f)).thenReturn(scoredParent1);
        when(scoredOrganismRepository.getRandomFromBottomPercent(0.9f)).thenReturn(scoredParent2);

        // When
        List<ScoredOrganism> parents = experiment.selectParents();

        // Then
        assertNotNull(parents);
        assertEquals(2, parents.size());
        assertEquals(scoredParent1, parents.get(0));
        assertEquals(scoredParent2, parents.get(1));
        verify(scoredOrganismRepository, times(1)).getRandomFromTopPercent(0.1f);
        verify(scoredOrganismRepository, times(1)).getRandomFromBottomPercent(0.9f);
    }

    @Test
    void testBreedParents() {
        // Given
        Organism parent1 = new Organism("parent1");
        Organism parent2 = new Organism("parent2");
        Organism child1 = new Organism("child1");
        Organism child2 = new Organism("child2");
        List<Organism> parents = List.of(parent1, parent2);
        List<Organism> children = List.of(child1, child2);

        when(organismBreeder.breed(parents)).thenReturn(children);

        // When
        List<Organism> result = experiment.breedParents(parents);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(children, result);
        verify(organismBreeder, times(1)).breed(parents);
    }

    @Test
    void testMutateChildrenWithMutationalOrganisms() {
        // Given
        Organism child1 = mock(Organism.class, withSettings().extraInterfaces(Mutational.class));
        Organism child2 = mock(Organism.class, withSettings().extraInterfaces(Mutational.class));
        List<Organism> children = List.of(child1, child2);

        MutationCommand mutation1 = mock(MutationCommand.class);
        MutationCommand mutation2 = mock(MutationCommand.class);
        List<MutationCommand> mutations = List.of(mutation1, mutation2);

        when(((Mutational) child1).getMutationCommandList()).thenReturn(mutations);
        when(((Mutational) child2).getMutationCommandList()).thenReturn(mutations);

        // When
        experiment.mutateChildren(children);

        // Then
        verify((Mutational) child1, atLeastOnce()).getMutationCommandList();
        verify((Mutational) child2, atLeastOnce()).getMutationCommandList();
        verify(mutation1, atLeastOnce()).execute();
        verify(mutation2, atLeastOnce()).execute();
    }

    @Test
    void testMutateChildrenWithNonMutationalOrganisms() {
        // Given
        Organism child1 = new Organism("child1");
        List<Organism> children = List.of(child1);

        // When/Then - should not throw exception
        assertDoesNotThrow(() -> experiment.mutateChildren(children));
    }

    @Test
    void testEvaluateChildren() {
        // Given
        Organism child1 = new Organism("child1");
        Organism child2 = new Organism("child2");
        List<Organism> children = List.of(child1, child2);

        // Use ArgumentMatchers to match by organism ID with null check
        when(evaluator.evaluate(argThat(o -> o != null && o.getId().equals("child1")))).thenReturn(2.5);
        when(evaluator.evaluate(argThat(o -> o != null && o.getId().equals("child2")))).thenReturn(3.7);

        // When
        List<ScoredOrganism> result = experiment.evaluateChildren(children);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Extract the scores from the result list
        Set<Double> scores = result.stream()
                .map(ScoredOrganism::score)
                .collect(java.util.stream.Collectors.toSet());
        
        // Verify that both expected scores are present
        assertTrue(scores.contains(2.5), "Result should contain a score of 2.5");
        assertTrue(scores.contains(3.7), "Result should contain a score of 3.7");
        
        // Verify that each child organism is in the result with the correct score
        for (ScoredOrganism scoredOrganism : result) {
            if (scoredOrganism.organismId().equals(child1.getId())) {
                assertEquals(2.5, scoredOrganism.score());
                assertEquals(child1, scoredOrganism.organism());
            } else if (scoredOrganism.organismId().equals(child2.getId())) {
                assertEquals(3.7, scoredOrganism.score());
                assertEquals(child2, scoredOrganism.organism());
            } else {
                fail("Unexpected organism in result: " + scoredOrganism.organismId());
            }
        }
        
        verify(evaluator, times(1)).evaluate(argThat(o -> o != null && o.getId().equals("child1")));
        verify(evaluator, times(1)).evaluate(argThat(o -> o != null && o.getId().equals("child2")));
    }

    @Test
    void testMaintainRepositoryWhenBothParentsAreTopTwo() {
        // Given - parents have best scores
        Organism parent1Org = new Organism("parent1");
        Organism parent2Org = new Organism("parent2");
        Organism child1Org = new Organism("child1");
        Organism child2Org = new Organism("child2");

        ScoredOrganism parent1 = new ScoredOrganism("sp1", 1.0, "parent1", parent1Org);
        ScoredOrganism parent2 = new ScoredOrganism("sp2", 2.0, "parent2", parent2Org);
        ScoredOrganism child1 = new ScoredOrganism(null, 5.0, "child1", child1Org);
        ScoredOrganism child2 = new ScoredOrganism(null, 6.0, "child2", child2Org);

        List<ScoredOrganism> parents = List.of(parent1, parent2);
        List<ScoredOrganism> children = List.of(child1, child2);

        // When
        experiment.maintainRepository(parents, children);

        // Then - no changes should be made
        verify(scoredOrganismRepository, never()).delete(any());
        verify(organismRepository, never()).deleteOrganism(any());
        verify(scoredOrganismRepository, never()).save(any());
        verify(organismRepository, never()).saveOrganism(any());
    }

    @Test
    void testMaintainRepositoryWhenOneChildIsInTopTwo() {
        // Given - one parent and one child in top two
        Organism parent1Org = new Organism("parent1");
        Organism parent2Org = new Organism("parent2");
        Organism child1Org = new Organism("child1");
        Organism child2Org = new Organism("child2");

        ScoredOrganism parent1 = new ScoredOrganism("sp1", 1.0, "parent1", parent1Org);
        ScoredOrganism parent2 = new ScoredOrganism("sp2", 5.0, "parent2", parent2Org);
        ScoredOrganism child1 = new ScoredOrganism(null, 2.0, "child1", child1Org);
        ScoredOrganism child2 = new ScoredOrganism(null, 6.0, "child2", child2Org);

        List<ScoredOrganism> parents = List.of(parent1, parent2);
        List<ScoredOrganism> children = List.of(child1, child2);

        when(organismRepository.saveOrganism(child1Org)).thenReturn(child1Org);
        when(scoredOrganismRepository.save(any(ScoredOrganism.class)))
                .thenAnswer(invocation -> {
                    ScoredOrganism arg = invocation.getArgument(0);
                    return new ScoredOrganism("new-id", arg.score(), arg.organismId(), arg.organism());
                });

        // When
        experiment.maintainRepository(parents, children);

        // Then - worst parent should be replaced
        verify(scoredOrganismRepository, times(1)).delete(parent2.id());
        verify(organismRepository, times(1)).deleteOrganism(parent2.organismId());
        verify(organismRepository, times(1)).saveOrganism(child1Org);
        verify(scoredOrganismRepository, times(1)).save(any(ScoredOrganism.class));
    }

    @Test
    void testMaintainRepositoryWhenBothChildrenAreInTopTwo() {
        // Given - both children have better scores than both parents
        Organism parent1Org = new Organism("parent1");
        Organism parent2Org = new Organism("parent2");
        Organism child1Org = new Organism("child1");
        Organism child2Org = new Organism("child2");

        ScoredOrganism parent1 = new ScoredOrganism("sp1", 5.0, "parent1", parent1Org);
        ScoredOrganism parent2 = new ScoredOrganism("sp2", 6.0, "parent2", parent2Org);
        ScoredOrganism child1 = new ScoredOrganism(null, 1.0, "child1", child1Org);
        ScoredOrganism child2 = new ScoredOrganism(null, 2.0, "child2", child2Org);

        List<ScoredOrganism> parents = List.of(parent1, parent2);
        List<ScoredOrganism> children = List.of(child1, child2);

        when(organismRepository.saveOrganism(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(scoredOrganismRepository.save(any(ScoredOrganism.class)))
                .thenAnswer(invocation -> {
                    ScoredOrganism arg = invocation.getArgument(0);
                    return new ScoredOrganism("new-id", arg.score(), arg.organismId(), arg.organism());
                });

        // When
        experiment.maintainRepository(parents, children);

        // Then - both parents should be replaced
        verify(scoredOrganismRepository, times(1)).delete(parent1.id());
        verify(scoredOrganismRepository, times(1)).delete(parent2.id());
        verify(organismRepository, times(1)).deleteOrganism(parent1.organismId());
        verify(organismRepository, times(1)).deleteOrganism(parent2.organismId());
        verify(organismRepository, times(2)).saveOrganism(any());
        verify(scoredOrganismRepository, times(2)).save(any(ScoredOrganism.class));
    }

    @Test
    void testMaintainRepositoryWithEmptyChildren() {
        // Given
        Organism parent1Org = new Organism("parent1");
        Organism parent2Org = new Organism("parent2");
        ScoredOrganism parent1 = new ScoredOrganism("sp1", 1.0, "parent1", parent1Org);
        ScoredOrganism parent2 = new ScoredOrganism("sp2", 2.0, "parent2", parent2Org);

        List<ScoredOrganism> parents = List.of(parent1, parent2);
        List<ScoredOrganism> children = new ArrayList<>();

        // When
        experiment.maintainRepository(parents, children);

        // Then - no changes should be made
        verify(scoredOrganismRepository, never()).delete(any());
        verify(organismRepository, never()).deleteOrganism(any());
    }

    @Test
    void testMaintainRepositoryWithInvalidParentCount() {
        // Given
        Organism parent1Org = new Organism("parent1");
        ScoredOrganism parent1 = new ScoredOrganism("sp1", 1.0, "parent1", parent1Org);
        Organism child1Org = new Organism("child1");
        ScoredOrganism child1 = new ScoredOrganism(null, 2.0, "child1", child1Org);

        List<ScoredOrganism> parents = List.of(parent1); // Only one parent
        List<ScoredOrganism> children = List.of(child1);

        // When
        experiment.maintainRepository(parents, children);

        // Then - no changes should be made
        verify(scoredOrganismRepository, never()).delete(any());
        verify(organismRepository, never()).deleteOrganism(any());
    }

    @Test
    void testMutationCycleIntegration() {
        // Given
        Organism parent1Org = new Organism("parent1");
        Organism parent2Org = new Organism("parent2");
        Organism child1Org = mock(Organism.class, withSettings().extraInterfaces(Mutational.class));
        Organism child2Org = mock(Organism.class, withSettings().extraInterfaces(Mutational.class));

        ScoredOrganism parent1 = new ScoredOrganism("sp1", 1.0, "parent1", parent1Org);
        ScoredOrganism parent2 = new ScoredOrganism("sp2", 5.0, "parent2", parent2Org);

        when(child1Org.getId()).thenReturn("child1");
        when(child2Org.getId()).thenReturn("child2");

        when(scoredOrganismRepository.getRandomFromTopPercent(0.1f)).thenReturn(parent1);
        when(scoredOrganismRepository.getRandomFromBottomPercent(0.9f)).thenReturn(parent2);
        when(organismBreeder.breed(any())).thenReturn(List.of(child1Org, child2Org));
        when(evaluator.evaluate(child1Org)).thenReturn(2.0);
        when(evaluator.evaluate(child2Org)).thenReturn(6.0);

        MutationCommand mutation = mock(MutationCommand.class);
        when(((Mutational) child1Org).getMutationCommandList()).thenReturn(List.of(mutation));
        when(((Mutational) child2Org).getMutationCommandList()).thenReturn(List.of(mutation));

        // Mock organismRepository.saveOrganism to return the organism passed to it
        when(organismRepository.saveOrganism(any())).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Mock scoredOrganismRepository.save to return a saved version with an ID
        when(scoredOrganismRepository.save(any(ScoredOrganism.class)))
                .thenAnswer(invocation -> {
                    ScoredOrganism arg = invocation.getArgument(0);
                    return new ScoredOrganism("new-id", arg.score(), arg.organismId(), arg.organism());
                });

        // When
        experiment.mutationCycle();

        // Then
        verify(scoredOrganismRepository).getRandomFromTopPercent(0.1f);
        verify(scoredOrganismRepository).getRandomFromBottomPercent(0.9f);
        verify(organismBreeder).breed(any());
        verify(evaluator).evaluate(child1Org);
        verify(evaluator).evaluate(child2Org);
        verify(mutation, atLeastOnce()).execute();
    }
}
