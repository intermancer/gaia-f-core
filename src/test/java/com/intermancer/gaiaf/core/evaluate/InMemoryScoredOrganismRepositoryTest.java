
package com.intermancer.gaiaf.core.evaluate;

import com.intermancer.gaiaf.core.organism.Organism;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryScoredOrganismRepositoryTest {

    private InMemoryScoredOrganismRepository repository;
    private Organism mockOrganism1;
    private Organism mockOrganism2;
    private Organism mockOrganism3;
    private static final String EXPERIMENT_ID_1 = "exp-1";
    private static final String EXPERIMENT_ID_2 = "exp-2";

    @BeforeEach
    void setUp() {
        repository = new InMemoryScoredOrganismRepository();
        mockOrganism1 = new Organism("organism-1");
        mockOrganism2 = new Organism("organism-2");
        mockOrganism3 = new Organism("organism-3");
    }

    // ===== Basic Save/Retrieve Tests =====

    @Test
    @DisplayName("save() should generate ID when ScoredOrganism has no ID")
    void testSaveGeneratesId() {
        ScoredOrganism organismWithoutId = new ScoredOrganism(
                null, 10.0, "organism-1", mockOrganism1, EXPERIMENT_ID_1
        );

        ScoredOrganism saved = repository.save(organismWithoutId);

        assertNotNull(saved.id());
        assertFalse(saved.id().isEmpty());
        assertEquals(10.0, saved.score());
        assertEquals("organism-1", saved.organismId());
        assertEquals(EXPERIMENT_ID_1, saved.experimentId());
    }

    @Test
    @DisplayName("save() should preserve ID when ScoredOrganism has an ID")
    void testSavePreservesId() {
        ScoredOrganism organismWithId = new ScoredOrganism(
                "custom-id", 10.0, "organism-1", mockOrganism1, EXPERIMENT_ID_1
        );

        ScoredOrganism saved = repository.save(organismWithId);

        assertEquals("custom-id", saved.id());
        assertEquals(10.0, saved.score());
        assertEquals(EXPERIMENT_ID_1, saved.experimentId());
    }

    @Test
    @DisplayName("getById() should retrieve saved organism")
    void testGetById() {
        ScoredOrganism saved = repository.save(
                new ScoredOrganism(10.0, mockOrganism1, EXPERIMENT_ID_1)
        );

        ScoredOrganism retrieved = repository.getById(saved.id());

        assertEquals(saved.id(), retrieved.id());
        assertEquals(saved.score(), retrieved.score());
        assertEquals(saved.organismId(), retrieved.organismId());
        assertEquals(saved.experimentId(), retrieved.experimentId());
    }

    @Test
    @DisplayName("getById() should throw exception when ID not found")
    void testGetByIdThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.getById("non-existent-id")
        );

        assertTrue(exception.getMessage().contains("No ScoredOrganism found with ID"));
    }

    // ===== Delete Tests =====

    @Test
    @DisplayName("delete() should remove organism from repository")
    void testDelete() {
        ScoredOrganism saved = repository.save(
                new ScoredOrganism(10.0, mockOrganism1, EXPERIMENT_ID_1)
        );

        repository.delete(saved.id());

        assertThrows(IllegalArgumentException.class, () -> repository.getById(saved.id()));
    }

    @Test
    @DisplayName("delete() should throw exception when ID not found")
    void testDeleteThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.delete("non-existent-id")
        );

        assertTrue(exception.getMessage().contains("No ScoredOrganism found with ID"));
    }

    @Test
    @DisplayName("delete() should handle organisms with duplicate scores")
    void testDeleteWithDuplicateScores() {
        ScoredOrganism first = repository.save(
                new ScoredOrganism(10.0, mockOrganism1, EXPERIMENT_ID_1)
        );
        ScoredOrganism second = repository.save(
                new ScoredOrganism(10.0, mockOrganism2, EXPERIMENT_ID_1)
        );
        ScoredOrganism third = repository.save(
                new ScoredOrganism(10.0, mockOrganism3, EXPERIMENT_ID_1)
        );

        repository.delete(second.id());

        assertThrows(IllegalArgumentException.class, () -> repository.getById(second.id()));
        assertDoesNotThrow(() -> repository.getById(first.id()));
        assertDoesNotThrow(() -> repository.getById(third.id()));
    }

    // ===== Experiment Isolation Tests =====

    @Test
    @DisplayName("save() should maintain separate ordered lists for different experiments")
    void testSeparateExperimentLists() {
        repository.save(new ScoredOrganism(1.0, mockOrganism1, EXPERIMENT_ID_1));
        repository.save(new ScoredOrganism(2.0, mockOrganism2, EXPERIMENT_ID_1));
        repository.save(new ScoredOrganism(10.0, mockOrganism3, EXPERIMENT_ID_2));

        assertEquals(3, repository.size());
        assertEquals(2, repository.size(EXPERIMENT_ID_1));
        assertEquals(1, repository.size(EXPERIMENT_ID_2));
    }

    @Test
    @DisplayName("getAllOrganismIds() should return only IDs for specified experiment")
    void testGetAllOrganismIdsByExperiment() {
        ScoredOrganism exp1_org1 = repository.save(
                new ScoredOrganism(1.0, mockOrganism1, EXPERIMENT_ID_1)
        );
        ScoredOrganism exp1_org2 = repository.save(
                new ScoredOrganism(2.0, mockOrganism2, EXPERIMENT_ID_1)
        );
        ScoredOrganism exp2_org1 = repository.save(
                new ScoredOrganism(10.0, mockOrganism3, EXPERIMENT_ID_2)
        );

        List<String> exp1Ids = repository.getAllOrganismIds(EXPERIMENT_ID_1);
        List<String> exp2Ids = repository.getAllOrganismIds(EXPERIMENT_ID_2);

        assertEquals(2, exp1Ids.size());
        assertTrue(exp1Ids.contains(exp1_org1.id()));
        assertTrue(exp1Ids.contains(exp1_org2.id()));
        assertFalse(exp1Ids.contains(exp2_org1.id()));

        assertEquals(1, exp2Ids.size());
        assertTrue(exp2Ids.contains(exp2_org1.id()));
    }

    @Test
    @DisplayName("getAllOrganismIds() should return empty list for non-existent experiment")
    void testGetAllOrganismIdsNonExistentExperiment() {
        repository.save(new ScoredOrganism(1.0, mockOrganism1, EXPERIMENT_ID_1));

        List<String> ids = repository.getAllOrganismIds("non-existent-exp");

        assertNotNull(ids);
        assertTrue(ids.isEmpty());
    }

    // ===== Size Tests =====

    @Test
    @DisplayName("size() should return total count across all experiments")
    void testSizeTotal() {
        assertEquals(0, repository.size());

        repository.save(new ScoredOrganism(1.0, mockOrganism1, EXPERIMENT_ID_1));
        assertEquals(1, repository.size());

        repository.save(new ScoredOrganism(2.0, mockOrganism2, EXPERIMENT_ID_1));
        assertEquals(2, repository.size());

        repository.save(new ScoredOrganism(10.0, mockOrganism3, EXPERIMENT_ID_2));
        assertEquals(3, repository.size());
    }

    @Test
    @DisplayName("size(experimentId) should return count for specific experiment")
    void testSizeByExperiment() {
        repository.save(new ScoredOrganism(1.0, mockOrganism1, EXPERIMENT_ID_1));
        repository.save(new ScoredOrganism(2.0, mockOrganism2, EXPERIMENT_ID_1));
        repository.save(new ScoredOrganism(10.0, mockOrganism3, EXPERIMENT_ID_2));

        assertEquals(2, repository.size(EXPERIMENT_ID_1));
        assertEquals(1, repository.size(EXPERIMENT_ID_2));
        assertEquals(0, repository.size("non-existent-exp"));
    }

    // ===== getRandomFromTopPercent Tests (without experimentId) =====

    @Test
    @DisplayName("getRandomFromTopPercent() should return organism from top percentage across all experiments")
    void testGetRandomFromTopPercentAllExperiments() {
        // Create 5 organisms in exp1 with scores 1-5
        for (int i = 1; i <= 5; i++) {
            repository.save(new ScoredOrganism((double) i,
                    new Organism("exp1-org-" + i), EXPERIMENT_ID_1));
        }
        // Create 5 organisms in exp2 with scores 6-10
        for (int i = 6; i <= 10; i++) {
            repository.save(new ScoredOrganism((double) i,
                    new Organism("exp2-org-" + i), EXPERIMENT_ID_2));
        }

        // Get from top 20% (best performers - should be scores 1.0 and 2.0)
        ScoredOrganism topOrganism = repository.getRandomFromTopPercent(0.2f);

        assertTrue(topOrganism.score() <= 2.0);
    }

    @Test
    @DisplayName("getRandomFromTopPercent() should throw exception when repository is empty")
    void testGetRandomFromTopPercentEmptyRepository() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.getRandomFromTopPercent(0.1f)
        );

        assertTrue(exception.getMessage().contains("Repository is empty"));
    }

    @Test
    @DisplayName("getRandomFromTopPercent() should throw exception for invalid percent")
    void testGetRandomFromTopPercentInvalidPercent() {
        repository.save(new ScoredOrganism(10.0, mockOrganism1, EXPERIMENT_ID_1));

        assertThrows(IllegalArgumentException.class,
                () -> repository.getRandomFromTopPercent(-0.1f));
        assertThrows(IllegalArgumentException.class,
                () -> repository.getRandomFromTopPercent(1.5f));
    }

    // ===== getRandomFromBottomPercent Tests (without experimentId) =====

    @Test
    @DisplayName("getRandomFromBottomPercent() should return organism from bottom percentage across all experiments")
    void testGetRandomFromBottomPercentAllExperiments() {
        // Create 5 organisms in exp1 with scores 1-5
        for (int i = 1; i <= 5; i++) {
            repository.save(new ScoredOrganism((double) i,
                    new Organism("exp1-org-" + i), EXPERIMENT_ID_1));
        }
        // Create 5 organisms in exp2 with scores 6-10
        for (int i = 6; i <= 10; i++) {
            repository.save(new ScoredOrganism((double) i,
                    new Organism("exp2-org-" + i), EXPERIMENT_ID_2));
        }

        // Get from bottom 20% (worst performers - should be scores 9.0 and 10.0)
        ScoredOrganism bottomOrganism = repository.getRandomFromBottomPercent(0.2f);

        assertTrue(bottomOrganism.score() >= 9.0);
    }

    @Test
    @DisplayName("getRandomFromBottomPercent() should throw exception when repository is empty")
    void testGetRandomFromBottomPercentEmptyRepository() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.getRandomFromBottomPercent(0.9f)
        );

        assertTrue(exception.getMessage().contains("Repository is empty"));
    }

    @Test
    @DisplayName("getRandomFromBottomPercent() should throw exception for invalid percent")
    void testGetRandomFromBottomPercentInvalidPercent() {
        repository.save(new ScoredOrganism(10.0, mockOrganism1, EXPERIMENT_ID_1));

        assertThrows(IllegalArgumentException.class,
                () -> repository.getRandomFromBottomPercent(-0.1f));
        assertThrows(IllegalArgumentException.class,
                () -> repository.getRandomFromBottomPercent(1.5f));
    }

    // ===== getRandomFromTopPercent Tests (with experimentId) =====

    @Test
    @DisplayName("getRandomFromTopPercent(experimentId) should return organism from top percentage of specific experiment")
    void testGetRandomFromTopPercentByExperiment() {
        // Create organisms in exp1 with scores 1-5
        for (int i = 1; i <= 5; i++) {
            repository.save(new ScoredOrganism((double) i,
                    new Organism("exp1-org-" + i), EXPERIMENT_ID_1));
        }
        // Create organisms in exp2 with scores 10-14
        for (int i = 10; i <= 14; i++) {
            repository.save(new ScoredOrganism((double) i,
                    new Organism("exp2-org-" + i), EXPERIMENT_ID_2));
        }

        // Get from top 40% of exp1 (should be scores 1.0 and 2.0)
        ScoredOrganism exp1Top = repository.getRandomFromTopPercent(EXPERIMENT_ID_1, 0.4f);
        assertTrue(exp1Top.score() <= 2.0);
        assertEquals(EXPERIMENT_ID_1, exp1Top.experimentId());

        // Get from top 40% of exp2 (should be scores 10.0 and 11.0)
        ScoredOrganism exp2Top = repository.getRandomFromTopPercent(EXPERIMENT_ID_2, 0.4f);
        assertTrue(exp2Top.score() <= 11.0);
        assertEquals(EXPERIMENT_ID_2, exp2Top.experimentId());
    }

    @Test
    @DisplayName("getRandomFromTopPercent(experimentId) should throw exception for empty experiment")
    void testGetRandomFromTopPercentByExperimentEmpty() {
        repository.save(new ScoredOrganism(1.0, mockOrganism1, EXPERIMENT_ID_1));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.getRandomFromTopPercent(EXPERIMENT_ID_2, 0.1f)
        );

        assertTrue(exception.getMessage().contains("Repository is empty for experimentId"));
    }

    // ===== getRandomFromBottomPercent Tests (with experimentId) =====

    @Test
    @DisplayName("getRandomFromBottomPercent(experimentId) should return organism from bottom percentage of specific experiment")
    void testGetRandomFromBottomPercentByExperiment() {
        // Create organisms in exp1 with scores 1-5
        for (int i = 1; i <= 5; i++) {
            repository.save(new ScoredOrganism((double) i,
                    new Organism("exp1-org-" + i), EXPERIMENT_ID_1));
        }
        // Create organisms in exp2 with scores 10-14
        for (int i = 10; i <= 14; i++) {
            repository.save(new ScoredOrganism((double) i,
                    new Organism("exp2-org-" + i), EXPERIMENT_ID_2));
        }

        // Get from bottom 40% of exp1 (should be scores 4.0 and 5.0)
        ScoredOrganism exp1Bottom = repository.getRandomFromBottomPercent(EXPERIMENT_ID_1, 0.4f);
        assertTrue(exp1Bottom.score() >= 4.0);
        assertEquals(EXPERIMENT_ID_1, exp1Bottom.experimentId());

        // Get from bottom 40% of exp2 (should be scores 13.0 and 14.0)
        ScoredOrganism exp2Bottom = repository.getRandomFromBottomPercent(EXPERIMENT_ID_2, 0.4f);
        assertTrue(exp2Bottom.score() >= 13.0);
        assertEquals(EXPERIMENT_ID_2, exp2Bottom.experimentId());
    }

    @Test
    @DisplayName("getRandomFromBottomPercent(experimentId) should throw exception for empty experiment")
    void testGetRandomFromBottomPercentByExperimentEmpty() {
        repository.save(new ScoredOrganism(1.0, mockOrganism1, EXPERIMENT_ID_1));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.getRandomFromBottomPercent(EXPERIMENT_ID_2, 0.9f)
        );

        assertTrue(exception.getMessage().contains("Repository is empty for experimentId"));
    }

    // ===== Distribution Tests =====

    @Test
    @DisplayName("Percentage distribution should work correctly within single experiment")
    void testPercentageDistributionSingleExperiment() {
        // Create 100 organisms with scores 1.0 to 100.0
        for (int i = 1; i <= 100; i++) {
            repository.save(new ScoredOrganism((double) i,
                    new Organism("organism-" + i), EXPERIMENT_ID_1));
        }

        // Test top 10% (best performers - lowest scores)
        Set<Double> topScores = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            ScoredOrganism top = repository.getRandomFromTopPercent(EXPERIMENT_ID_1, 0.1f);
            topScores.add(top.score());
        }
        assertTrue(topScores.stream().allMatch(score -> score <= 10.0));

        // Test bottom 10% (worst performers - highest scores)
        Set<Double> bottomScores = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            ScoredOrganism bottom = repository.getRandomFromBottomPercent(EXPERIMENT_ID_1, 0.1f);
            bottomScores.add(bottom.score());
        }
        assertTrue(bottomScores.stream().allMatch(score -> score >= 91.0));
    }

    // ===== Edge Case Tests =====

    @Test
    @DisplayName("Repository should handle single organism correctly")
    void testSingleOrganism() {
        ScoredOrganism saved = repository.save(
                new ScoredOrganism(5.0, mockOrganism1, EXPERIMENT_ID_1)
        );

        assertEquals(saved, repository.getRandomFromTopPercent(EXPERIMENT_ID_1, 1.0f));
        assertEquals(saved, repository.getRandomFromBottomPercent(EXPERIMENT_ID_1, 1.0f));
        assertEquals(saved, repository.getRandomFromTopPercent(EXPERIMENT_ID_1, 0.5f));
        assertEquals(saved, repository.getRandomFromBottomPercent(EXPERIMENT_ID_1, 0.5f));
    }

    @Test
    @DisplayName("Repository should handle extreme percentages")
    void testExtremePercentages() {
        for (int i = 1; i <= 10; i++) {
            repository.save(new ScoredOrganism((double) i,
                    new Organism("organism-" + i), EXPERIMENT_ID_1));
        }

        // Test 0% edge case - should still return an organism
        assertDoesNotThrow(() -> repository.getRandomFromTopPercent(EXPERIMENT_ID_1, 0.0f));
        assertDoesNotThrow(() -> repository.getRandomFromBottomPercent(EXPERIMENT_ID_1, 0.0f));

        // Test 100% - should be able to select from entire experiment
        ScoredOrganism top100 = repository.getRandomFromTopPercent(EXPERIMENT_ID_1, 1.0f);
        assertTrue(top100.score() >= 1.0 && top100.score() <= 10.0);

        ScoredOrganism bottom100 = repository.getRandomFromBottomPercent(EXPERIMENT_ID_1, 1.0f);
        assertTrue(bottom100.score() >= 1.0 && bottom100.score() <= 10.0);
    }

    @Test
    @DisplayName("Delete should maintain experiment isolation")
    void testDeleteMaintainsExperimentIsolation() {
        ScoredOrganism exp1_org = repository.save(
                new ScoredOrganism(1.0, mockOrganism1, EXPERIMENT_ID_1)
        );
        ScoredOrganism exp2_org = repository.save(
                new ScoredOrganism(2.0, mockOrganism2, EXPERIMENT_ID_2)
        );

        repository.delete(exp1_org.id());

        assertEquals(0, repository.size(EXPERIMENT_ID_1));
        assertEquals(1, repository.size(EXPERIMENT_ID_2));
        assertDoesNotThrow(() -> repository.getById(exp2_org.id()));
    }

    @Test
    @DisplayName("save() should maintain sorted order within experiment lists")
    void testMaintainsSortedOrderWithinExperiments() {
        repository.save(new ScoredOrganism(5.0, mockOrganism1, EXPERIMENT_ID_1));
        repository.save(new ScoredOrganism(15.0, mockOrganism2, EXPERIMENT_ID_1));
        repository.save(new ScoredOrganism(10.0, mockOrganism3, EXPERIMENT_ID_1));

        // Verify by checking that top performer has lowest score
        // The top 34% of 3 organisms is 1 organism, which should be the one with lowest score (5.0)
        ScoredOrganism fromTop = repository.getRandomFromTopPercent(EXPERIMENT_ID_1, 0.34f);
        assertEquals(5.0, fromTop.score(), "Top performer should have lowest score (5.0)");
    }
}