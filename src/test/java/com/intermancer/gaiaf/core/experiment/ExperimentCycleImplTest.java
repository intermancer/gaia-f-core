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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for ExperimentCycleImpl.
 * Tests all experiment cycle phases with experimentId tracking.
 */
@ExtendWith(MockitoExtension.class)
class ExperimentCycleImplTest {

    private static final String TEST_EXPERIMENT_ID = "test-exp-123";

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
    void testSelectParents_passesExperimentId() {
        // Given
        Organism parent1 = new Organism("parent1");
        Organism parent2 = new Organism("parent2");
        ScoredOrganism scoredParent1 = new ScoredOrganism("sp1", 1.0, "parent1", parent1, TEST_EXPERIMENT_ID);
        ScoredOrganism scoredParent2 = new ScoredOrganism("sp2", 5.0, "parent2", parent2, TEST_EXPERIMENT_ID);

        when(scoredOrganismRepository.getRandomFromTopPercent(TEST_EXPERIMENT_ID, 0.1f)).thenReturn(scoredParent1);
        when(scoredOrganismRepository.getRandomFromBottomPercent(TEST_EXPERIMENT_ID, 0.9f)).thenReturn(scoredParent2);

        // When
        List<ScoredOrganism> parents = experimentCycle.selectParents(TEST_EXPERIMENT_ID);

        // Then
        assertNotNull(parents);
        assertEquals(2, parents.size());
        assertEquals(scoredParent1, parents.get(0));
        assertEquals(scoredParent2, parents.get(1));
        verify(scoredOrganismRepository, times(1)).getRandomFromTopPercent(TEST_EXPERIMENT_ID, 0.1f);
        verify(scoredOrganismRepository, times(1)).getRandomFromBottomPercent(TEST_EXPERIMENT_ID, 0.9f);
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
        verify(child1, atLeastOnce()).getMutationCommandList();
        verify(child2, atLeastOnce()).getMutationCommandList();
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
    void testEvaluateChildren_includesExperimentId() {
        // Given
        Organism child1 = new Organism("child1");
        Organism child2 = new Organism("child2");
        List<Organism> children = List.of(child1, child2);

        when(evaluator.evaluate(argThat(o -> o != null && o.getId().equals("child1")))).thenReturn(2.5);
        when(evaluator.evaluate(argThat(o -> o != null && o.getId().equals("child2")))).thenReturn(3.7);

        // When
        List<ScoredOrganism> result = experimentCycle.evaluateChildren(children, TEST_EXPERIMENT_ID);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify that each scored organism has the correct experimentId
        for (ScoredOrganism scoredOrganism : result) {
            assertEquals(TEST_EXPERIMENT_ID, scoredOrganism.experimentId());
            assertNotNull(scoredOrganism.id(), "New ScoredOrganism should have generated UUID id");
        }

        verify(evaluator, times(1)).evaluate(argThat(o -> o != null && o.getId().equals("child1")));
        verify(evaluator, times(1)).evaluate(argThat(o -> o != null && o.getId().equals("child2")));
    }

    @Test
    void testMaintainRepositoryWhenNotAtCapacity_usesExperimentIdForSizeCheck() {
        // Given - repository not at capacity
        Organism parent1Org = new Organism("parent1");
        Organism parent2Org = new Organism("parent2");
        Organism child1Org = new Organism("child1");
        Organism child2Org = new Organism("child2");

        ScoredOrganism parent1 = new ScoredOrganism("sp1", 1.0, "parent1", parent1Org, TEST_EXPERIMENT_ID);
        ScoredOrganism parent2 = new ScoredOrganism("sp2", 2.0, "parent2", parent2Org, TEST_EXPERIMENT_ID);
        ScoredOrganism child1 = new ScoredOrganism(null, 5.0, "child1", child1Org, TEST_EXPERIMENT_ID);
        ScoredOrganism child2 = new ScoredOrganism(null, 6.0, "child2", child2Org, TEST_EXPERIMENT_ID);

        List<ScoredOrganism> parents = List.of(parent1, parent2);
        List<ScoredOrganism> children = List.of(child1, child2);

        when(scoredOrganismRepository.size(TEST_EXPERIMENT_ID)).thenReturn(10);
        when(experimentConfiguration.getRepoCapacity()).thenReturn(50);
        when(organismRepository.saveOrganism(any())).thenAnswer(inv -> inv.getArgument(0));
        when(scoredOrganismRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When
        experimentCycle.maintainRepository(parents, children, TEST_EXPERIMENT_ID);

        // Then - children should be added, no replacements tracked
        verify(scoredOrganismRepository).size(TEST_EXPERIMENT_ID);
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

        ScoredOrganism parent1 = new ScoredOrganism("sp1", 1.0, "parent1", parent1Org, TEST_EXPERIMENT_ID);
        ScoredOrganism parent2 = new ScoredOrganism("sp2", 2.0, "parent2", parent2Org, TEST_EXPERIMENT_ID);
        ScoredOrganism child1 = new ScoredOrganism(null, 5.0, "child1", child1Org, TEST_EXPERIMENT_ID);
        ScoredOrganism child2 = new ScoredOrganism(null, 6.0, "child2", child2Org, TEST_EXPERIMENT_ID);

        List<ScoredOrganism> parents = List.of(parent1, parent2);
        List<ScoredOrganism> children = List.of(child1, child2);

        when(scoredOrganismRepository.size(TEST_EXPERIMENT_ID)).thenReturn(50);
        when(experimentConfiguration.getRepoCapacity()).thenReturn(50);

        // When
        experimentCycle.maintainRepository(parents, children, TEST_EXPERIMENT_ID);

        // Then - no changes should be made
        verify(scoredOrganismRepository).size(TEST_EXPERIMENT_ID);
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

        ScoredOrganism parent1 = new ScoredOrganism("sp1", 1.0, "parent1", parent1Org, TEST_EXPERIMENT_ID);
        ScoredOrganism parent2 = new ScoredOrganism("sp2", 5.0, "parent2", parent2Org, TEST_EXPERIMENT_ID);
        ScoredOrganism child1 = new ScoredOrganism(null, 2.0, "child1", child1Org, TEST_EXPERIMENT_ID);
        ScoredOrganism child2 = new ScoredOrganism(null, 6.0, "child2", child2Org, TEST_EXPERIMENT_ID);

        List<ScoredOrganism> parents = List.of(parent1, parent2);
        List<ScoredOrganism> children = List.of(child1, child2);

        when(scoredOrganismRepository.size(TEST_EXPERIMENT_ID)).thenReturn(50);
        when(experimentConfiguration.getRepoCapacity()).thenReturn(50);
        when(organismRepository.saveOrganism(child1Org)).thenReturn(child1Org);
        when(scoredOrganismRepository.save(any(ScoredOrganism.class)))
                .thenAnswer(invocation -> {
                    ScoredOrganism arg = invocation.getArgument(0);
                    return new ScoredOrganism("new-id", arg.score(), arg.organismId(), arg.organism(), arg.experimentId());
                });

        // When
        experimentCycle.maintainRepository(parents, children, TEST_EXPERIMENT_ID);

        // Then - worst parent should be replaced, organismsReplaced incremented by 1
        verify(scoredOrganismRepository).size(TEST_EXPERIMENT_ID);
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

        ScoredOrganism parent1 = new ScoredOrganism("sp1", 5.0, "parent1", parent1Org, TEST_EXPERIMENT_ID);
        ScoredOrganism parent2 = new ScoredOrganism("sp2", 6.0, "parent2", parent2Org, TEST_EXPERIMENT_ID);
        ScoredOrganism child1 = new ScoredOrganism(null, 1.0, "child1", child1Org, TEST_EXPERIMENT_ID);
        ScoredOrganism child2 = new ScoredOrganism(null, 2.0, "child2", child2Org, TEST_EXPERIMENT_ID);

        List<ScoredOrganism> parents = List.of(parent1, parent2);
        List<ScoredOrganism> children = List.of(child1, child2);

        when(scoredOrganismRepository.size(TEST_EXPERIMENT_ID)).thenReturn(50);
        when(experimentConfiguration.getRepoCapacity()).thenReturn(50);
        when(organismRepository.saveOrganism(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(scoredOrganismRepository.save(any(ScoredOrganism.class)))
                .thenAnswer(invocation -> {
                    ScoredOrganism arg = invocation.getArgument(0);
                    return new ScoredOrganism("new-id", arg.score(), arg.organismId(), arg.organism(), arg.experimentId());
                });

        // When
        experimentCycle.maintainRepository(parents, children, TEST_EXPERIMENT_ID);

        // Then - both parents should be replaced, organismsReplaced incremented by 2
        verify(scoredOrganismRepository).size(TEST_EXPERIMENT_ID);
        verify(scoredOrganismRepository, times(1)).delete(parent1.id());
        verify(scoredOrganismRepository, times(1)).delete(parent2.id());
        verify(organismRepository, times(1)).deleteOrganism(parent1.organismId());
        verify(organismRepository, times(1)).deleteOrganism(parent2.organismId());
        verify(organismRepository, times(2)).saveOrganism(any());
        verify(scoredOrganismRepository, times(2)).save(any(ScoredOrganism.class));
        assertEquals(2, experimentStatus.getOrganismsReplaced());
    }

    @Test
    void testMutationCycle_passesExperimentIdThroughAllPhases() {
        // Given
        Organism parent1Org = new Organism("parent1");
        Organism parent2Org = new Organism("parent2");
        Organism child1Org = mock(Organism.class, withSettings().extraInterfaces(Mutational.class));
        Organism child2Org = mock(Organism.class, withSettings().extraInterfaces(Mutational.class));

        ScoredOrganism parent1 = new ScoredOrganism("sp1", 1.0, "parent1", parent1Org, TEST_EXPERIMENT_ID);
        ScoredOrganism parent2 = new ScoredOrganism("sp2", 5.0, "parent2", parent2Org, TEST_EXPERIMENT_ID);

        when(child1Org.getId()).thenReturn("child1");
        when(child2Org.getId()).thenReturn("child2");

        when(scoredOrganismRepository.getRandomFromTopPercent(TEST_EXPERIMENT_ID, 0.1f)).thenReturn(parent1);
        when(scoredOrganismRepository.getRandomFromBottomPercent(TEST_EXPERIMENT_ID, 0.9f)).thenReturn(parent2);
        when(scoredOrganismRepository.size(TEST_EXPERIMENT_ID)).thenReturn(50);
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
                    return new ScoredOrganism("new-id", arg.score(), arg.organismId(), arg.organism(), arg.experimentId());
                });

        // When
        experimentCycle.mutationCycle(TEST_EXPERIMENT_ID);

        // Then - verify experimentId was used throughout
        verify(scoredOrganismRepository).getRandomFromTopPercent(TEST_EXPERIMENT_ID, 0.1f);
        verify(scoredOrganismRepository).getRandomFromBottomPercent(TEST_EXPERIMENT_ID, 0.9f);
        verify(scoredOrganismRepository).size(TEST_EXPERIMENT_ID);
        verify(organismBreeder).breed(any());
        verify(evaluator).evaluate(child1Org);
        verify(evaluator).evaluate(child2Org);
        verify(mutation, atLeastOnce()).execute();
        assertEquals(1, experimentStatus.getOrganismsReplaced());
    }

    @Test
    void testMutationCycle_notAtCapacity_noReplacementsTracked() {
        // Given - repository not at capacity, children should be added without replacement
        Organism parent1Org = new Organism("parent1");
        Organism parent2Org = new Organism("parent2");
        Organism child1Org = mock(Organism.class, withSettings().extraInterfaces(Mutational.class));
        Organism child2Org = mock(Organism.class, withSettings().extraInterfaces(Mutational.class));

        ScoredOrganism parent1 = new ScoredOrganism("sp1", 1.0, "parent1", parent1Org, TEST_EXPERIMENT_ID);
        ScoredOrganism parent2 = new ScoredOrganism("sp2", 5.0, "parent2", parent2Org, TEST_EXPERIMENT_ID);

        when(child1Org.getId()).thenReturn("child1");
        when(child2Org.getId()).thenReturn("child2");

        when(scoredOrganismRepository.getRandomFromTopPercent(TEST_EXPERIMENT_ID, 0.1f)).thenReturn(parent1);
        when(scoredOrganismRepository.getRandomFromBottomPercent(TEST_EXPERIMENT_ID, 0.9f)).thenReturn(parent2);
        when(scoredOrganismRepository.size(TEST_EXPERIMENT_ID)).thenReturn(10);
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
                    return new ScoredOrganism("new-id", arg.score(), arg.organismId(), arg.organism(), arg.experimentId());
                });

        // When
        experimentCycle.mutationCycle(TEST_EXPERIMENT_ID);

        // Then - children added but no replacements
        verify(scoredOrganismRepository).size(TEST_EXPERIMENT_ID);
        verify(organismRepository, times(2)).saveOrganism(any());
        verify(scoredOrganismRepository, times(2)).save(any());
        verify(scoredOrganismRepository, never()).delete(any());
        assertEquals(0, experimentStatus.getOrganismsReplaced());
    }

    @Test
    void testMaintainRepositoryWithEmptyChildren() {
        // Given
        Organism parent1Org = new Organism("parent1");
        Organism parent2Org = new Organism("parent2");
        ScoredOrganism parent1 = new ScoredOrganism("sp1", 1.0, "parent1", parent1Org, TEST_EXPERIMENT_ID);
        ScoredOrganism parent2 = new ScoredOrganism("sp2", 2.0, "parent2", parent2Org, TEST_EXPERIMENT_ID);

        List<ScoredOrganism> parents = List.of(parent1, parent2);
        List<ScoredOrganism> children = new ArrayList<>();

        // When
        experimentCycle.maintainRepository(parents, children, TEST_EXPERIMENT_ID);

        // Then - no changes should be made
        verify(scoredOrganismRepository, never()).delete(any());
        verify(organismRepository, never()).deleteOrganism(any());
        assertEquals(0, experimentStatus.getOrganismsReplaced());
    }

    @Test
    void testMaintainRepositoryWithInvalidParentCount() {
        // Given
        Organism parent1Org = new Organism("parent1");
        ScoredOrganism parent1 = new ScoredOrganism("sp1", 1.0, "parent1", parent1Org, TEST_EXPERIMENT_ID);
        Organism child1Org = new Organism("child1");
        ScoredOrganism child1 = new ScoredOrganism(null, 2.0, "child1", child1Org, TEST_EXPERIMENT_ID);

        List<ScoredOrganism> parents = List.of(parent1); // Only one parent
        List<ScoredOrganism> children = List.of(child1);

        // When
        experimentCycle.maintainRepository(parents, children, TEST_EXPERIMENT_ID);

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

        ScoredOrganism parent1 = new ScoredOrganism("sp1", 1.0, "parent1", parent1Org, TEST_EXPERIMENT_ID);
        ScoredOrganism parent2 = new ScoredOrganism("sp2", 5.0, "parent2", parent2Org, TEST_EXPERIMENT_ID);
        ScoredOrganism child1 = new ScoredOrganism(null, 2.0, "child1", child1Org, TEST_EXPERIMENT_ID);

        List<ScoredOrganism> parents = List.of(parent1, parent2);
        List<ScoredOrganism> children = List.of(child1);

        when(scoredOrganismRepository.size(TEST_EXPERIMENT_ID)).thenReturn(50);
        when(experimentConfiguration.getRepoCapacity()).thenReturn(50);
        when(organismRepository.saveOrganism(any())).thenAnswer(inv -> inv.getArgument(0));
        when(scoredOrganismRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When - run maintenance twice
        experimentCycle.maintainRepository(parents, children, TEST_EXPERIMENT_ID);
        experimentCycle.maintainRepository(parents, children, TEST_EXPERIMENT_ID);

        // Then - should accumulate to 2 replacements
        assertEquals(2, experimentStatus.getOrganismsReplaced());
    }
}