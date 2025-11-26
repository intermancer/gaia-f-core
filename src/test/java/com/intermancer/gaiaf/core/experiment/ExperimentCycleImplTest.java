
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for ExperimentCycleImpl.
 */
@ExtendWith(MockitoExtension.class)
class ExperimentCycleImplTest {

    @Mock
    private OrganismRepository organismRepository;

    @Mock
    private ScoredOrganismRepository scoredOrganismRepository;

    @Mock
    private OrganismBreeder organismBreeder;

    @Mock
    private Evaluator evaluator;

    @Mock
    private ExperimentConfiguration experimentConfiguration;

    @Spy
    private ExperimentStatus experimentStatus = new ExperimentStatus();

    private ExperimentCycleImpl experimentCycle;

    @BeforeEach
    void setUp() {
        experimentStatus.reset();
        experimentCycle = new ExperimentCycleImpl(
                organismRepository,
                scoredOrganismRepository,
                organismBreeder,
                evaluator,
                experimentConfiguration,
                experimentStatus
        );
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
        List<ScoredOrganism> parents = experimentCycle.selectParents();

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
        List<Organism> result = experimentCycle.breedParents(parents);

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

        when(child1.getMutationCommandList()).thenReturn(mutations);
        when(child2.getMutationCommandList()).thenReturn(mutations);

        // When
        experimentCycle.mutateChildren(children);

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
        assertDoesNotThrow(() -> experimentCycle.mutateChildren(children));
    }

    @Test
    void testMutateChildrenWithEmptyMutationList() {
        // Given
        Organism child1 = mock(Organism.class, withSettings().extraInterfaces(Mutational.class));
        List<Organism> children = List.of(child1);

        when(child1.getMutationCommandList()).thenReturn(new ArrayList<>());

        // When/Then - should not throw exception
        assertDoesNotThrow(() -> experimentCycle.mutateChildren(children));
        verify(child1, atLeastOnce()).getMutationCommandList();
    }

    @Test
    void testEvaluateChildren() {
        // Given
        Organism child1 = new Organism("child1");
        Organism child2 = new Organism("child2");
        List<Organism> children = List.of(child1, child2);

        when(evaluator.evaluate(argThat(o -> o != null && o.getId().equals("child1")))).thenReturn(2.5);
        when(evaluator.evaluate(argThat(o -> o != null && o.getId().equals("child2")))).thenReturn(3.7);

        // When
        List<ScoredOrganism> result = experimentCycle.evaluateChildren(children);

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
                assertNull(scoredOrganism.id(), "New ScoredOrganism should have null id before saving");
            } else if (scoredOrganism.organismId().equals(child2.getId())) {
                assertEquals(3.7, scoredOrganism.score());
                assertEquals(child2, scoredOrganism.organism());
                assertNull(scoredOrganism.id(), "New ScoredOrganism should have null id before saving");
            } else {
                fail("Unexpected organism in result: " + scoredOrganism.organismId());
            }
        }

        verify(evaluator, times(1)).evaluate(argThat(o -> o != null && o.getId().equals("child1")));
        verify(evaluator, times(1)).evaluate(argThat(o -> o != null && o.getId().equals("child2")));
    }

    @Test
    void testMaintainRepositoryWhenNotAtCapacity_addsChildrenWithoutReplacement() {
        // Given - repository not at capacity
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

        when(scoredOrganismRepository.size()).thenReturn(10);
        when(experimentConfiguration.getRepoCapacity()).thenReturn(50);
        when(organismRepository.saveOrganism(any())).thenAnswer(inv -> inv.getArgument(0));
        when(scoredOrganismRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When
        experimentCycle.maintainRepository(parents, children);

        // Then - children should be added, no replacements tracked
        verify(organismRepository, times(2)).saveOrganism(any());
        verify(scoredOrganismRepository, times(2)).save(any());
        assertEquals(0, experimentStatus.getOrganismsReplaced());
    }

    @Test
    void testMaintainRepositoryWhenBothParentsAreTopTwo_noReplacements() {
        // Given - parents have the best scores, repository at capacity
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

        when(scoredOrganismRepository.size()).thenReturn(50);
        when(experimentConfiguration.getRepoCapacity()).thenReturn(50);

        // When
        experimentCycle.maintainRepository(parents, children);

        // Then - no changes should be made
        verify(scoredOrganismRepository, never()).delete(any());
        verify(organismRepository, never()).deleteOrganism(any());
        verify(scoredOrganismRepository, never()).save(any());
        verify(organismRepository, never()).saveOrganism(any());
        assertEquals(0, experimentStatus.getOrganismsReplaced());
    }

    @Test
    void testMaintainRepositoryWhenOneChildIsInTopTwo_incrementsOrganismsReplacedByOne() {
        // Given - one parent and one child in the top two, repository at capacity
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

        when(scoredOrganismRepository.size()).thenReturn(50);
        when(experimentConfiguration.getRepoCapacity()).thenReturn(50);
        when(organismRepository.saveOrganism(child1Org)).thenReturn(child1Org);
        when(scoredOrganismRepository.save(any(ScoredOrganism.class)))
                .thenAnswer(invocation -> {
                    ScoredOrganism arg = invocation.getArgument(0);
                    return new ScoredOrganism("new-id", arg.score(), arg.organismId(), arg.organism());
                });

        // When
        experimentCycle.maintainRepository(parents, children);

        // Then - worst parent should be replaced, organismsReplaced incremented by 1
        verify(scoredOrganismRepository, times(1)).delete(parent2.id());
        verify(organismRepository, times(1)).deleteOrganism(parent2.organismId());
        verify(organismRepository, times(1)).saveOrganism(child1Org);
        verify(scoredOrganismRepository, times(1)).save(any(ScoredOrganism.class));
        assertEquals(1, experimentStatus.getOrganismsReplaced());
    }

    @Test
    void testMaintainRepositoryWhenBothChildrenAreInTopTwo_incrementsOrganismsReplacedByTwo() {
        // Given - both children have better scores than both parents, repository at capacity
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

        when(scoredOrganismRepository.size()).thenReturn(50);
        when(experimentConfiguration.getRepoCapacity()).thenReturn(50);
        when(organismRepository.saveOrganism(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(scoredOrganismRepository.save(any(ScoredOrganism.class)))
                .thenAnswer(invocation -> {
                    ScoredOrganism arg = invocation.getArgument(0);
                    return new ScoredOrganism("new-id", arg.score(), arg.organismId(), arg.organism());
                });

        // When
        experimentCycle.maintainRepository(parents, children);

        // Then - both parents should be replaced, organismsReplaced incremented by 2
        verify(scoredOrganismRepository, times(1)).delete(parent1.id());
        verify(scoredOrganismRepository, times(1)).delete(parent2.id());
        verify(organismRepository, times(1)).deleteOrganism(parent1.organismId());
        verify(organismRepository, times(1)).deleteOrganism(parent2.organismId());
        verify(organismRepository, times(2)).saveOrganism(any());
        verify(scoredOrganismRepository, times(2)).save(any(ScoredOrganism.class));
        assertEquals(2, experimentStatus.getOrganismsReplaced());
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
        experimentCycle.maintainRepository(parents, children);

        // Then - no changes should be made
        verify(scoredOrganismRepository, never()).delete(any());
        verify(organismRepository, never()).deleteOrganism(any());
        assertEquals(0, experimentStatus.getOrganismsReplaced());
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
        experimentCycle.maintainRepository(parents, children);

        // Then - no changes should be made
        verify(scoredOrganismRepository, never()).delete(any());
        verify(organismRepository, never()).deleteOrganism(any());
        assertEquals(0, experimentStatus.getOrganismsReplaced());
    }

    @Test
    void testMaintainRepository_multipleReplacements_accumulatesOrganismsReplaced() {
        // Given - run multiple maintenance cycles to accumulate replacements
        Organism parent1Org = new Organism("parent1");
        Organism parent2Org = new Organism("parent2");
        Organism child1Org = new Organism("child1");

        ScoredOrganism parent1 = new ScoredOrganism("sp1", 1.0, "parent1", parent1Org);
        ScoredOrganism parent2 = new ScoredOrganism("sp2", 5.0, "parent2", parent2Org);
        ScoredOrganism child1 = new ScoredOrganism(null, 2.0, "child1", child1Org);

        List<ScoredOrganism> parents = List.of(parent1, parent2);
        List<ScoredOrganism> children = List.of(child1);

        when(scoredOrganismRepository.size()).thenReturn(50);
        when(experimentConfiguration.getRepoCapacity()).thenReturn(50);
        when(organismRepository.saveOrganism(any())).thenAnswer(inv -> inv.getArgument(0));
        when(scoredOrganismRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When - run maintenance twice
        experimentCycle.maintainRepository(parents, children);
        experimentCycle.maintainRepository(parents, children);

        // Then - should accumulate to 2 replacements
        assertEquals(2, experimentStatus.getOrganismsReplaced());
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
        when(scoredOrganismRepository.size()).thenReturn(50);
        when(experimentConfiguration.getRepoCapacity()).thenReturn(50);
        when(organismBreeder.breed(any())).thenReturn(List.of(child1Org, child2Org));
        when(evaluator.evaluate(child1Org)).thenReturn(2.0);
        when(evaluator.evaluate(child2Org)).thenReturn(6.0);

        MutationCommand mutation = mock(MutationCommand.class);
        when(child1Org.getMutationCommandList()).thenReturn(List.of(mutation));
        when(child2Org.getMutationCommandList()).thenReturn(List.of(mutation));

        when(organismRepository.saveOrganism(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(scoredOrganismRepository.save(any(ScoredOrganism.class)))
                .thenAnswer(invocation -> {
                    ScoredOrganism arg = invocation.getArgument(0);
                    return new ScoredOrganism("new-id", arg.score(), arg.organismId(), arg.organism());
                });

        // When
        experimentCycle.mutationCycle();

        // Then
        verify(scoredOrganismRepository).getRandomFromTopPercent(0.1f);
        verify(scoredOrganismRepository).getRandomFromBottomPercent(0.9f);
        verify(organismBreeder).breed(any());
        verify(evaluator).evaluate(child1Org);
        verify(evaluator).evaluate(child2Org);
        verify(mutation, atLeastOnce()).execute();
        // One child replaced one parent
        assertEquals(1, experimentStatus.getOrganismsReplaced());
    }

    @Test
    void testMutationCycleWithRepositoryMaintenance_tracksReplacement() {
        // Given - scenario where one child replaces worst parent
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
        when(scoredOrganismRepository.size()).thenReturn(50);
        when(experimentConfiguration.getRepoCapacity()).thenReturn(50);
        when(organismBreeder.breed(any())).thenReturn(List.of(child1Org, child2Org));

        // Child1 performs better than parent2
        when(evaluator.evaluate(child1Org)).thenReturn(2.0);
        when(evaluator.evaluate(child2Org)).thenReturn(6.0);

        MutationCommand mutation = mock(MutationCommand.class);
        when(child1Org.getMutationCommandList()).thenReturn(List.of(mutation));
        when(child2Org.getMutationCommandList()).thenReturn(List.of(mutation));

        when(organismRepository.saveOrganism(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(scoredOrganismRepository.save(any(ScoredOrganism.class)))
                .thenAnswer(invocation -> {
                    ScoredOrganism arg = invocation.getArgument(0);
                    return new ScoredOrganism("new-id", arg.score(), arg.organismId(), arg.organism());
                });

        // When
        experimentCycle.mutationCycle();

        // Then - verify repository maintenance occurred and replacement was tracked
        verify(scoredOrganismRepository, times(1)).delete(parent2.id());
        verify(organismRepository, times(1)).deleteOrganism(parent2.organismId());
        verify(organismRepository, times(1)).saveOrganism(child1Org);
        verify(scoredOrganismRepository, times(1)).save(any(ScoredOrganism.class));
        assertEquals(1, experimentStatus.getOrganismsReplaced());
    }

    @Test
    void testMutationCycle_notAtCapacity_noReplacementsTracked() {
        // Given - repository not at capacity, children should be added without replacement
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
        when(scoredOrganismRepository.size()).thenReturn(10);
        when(experimentConfiguration.getRepoCapacity()).thenReturn(50);
        when(organismBreeder.breed(any())).thenReturn(List.of(child1Org, child2Org));
        when(evaluator.evaluate(child1Org)).thenReturn(2.0);
        when(evaluator.evaluate(child2Org)).thenReturn(6.0);

        MutationCommand mutation = mock(MutationCommand.class);
        when(child1Org.getMutationCommandList()).thenReturn(List.of(mutation));
        when(child2Org.getMutationCommandList()).thenReturn(List.of(mutation));

        when(organismRepository.saveOrganism(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(scoredOrganismRepository.save(any(ScoredOrganism.class)))
                .thenAnswer(invocation -> {
                    ScoredOrganism arg = invocation.getArgument(0);
                    return new ScoredOrganism("new-id", arg.score(), arg.organismId(), arg.organism());
                });

        // When
        experimentCycle.mutationCycle();

        // Then - children added but no replacements
        verify(organismRepository, times(2)).saveOrganism(any());
        verify(scoredOrganismRepository, times(2)).save(any());
        verify(scoredOrganismRepository, never()).delete(any());
        assertEquals(0, experimentStatus.getOrganismsReplaced());
    }
}