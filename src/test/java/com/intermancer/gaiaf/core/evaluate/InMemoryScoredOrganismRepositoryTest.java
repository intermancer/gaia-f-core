package com.intermancer.gaiaf.core.evaluate;

import com.intermancer.gaiaf.core.organism.Organism;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryScoredOrganismRepositoryTest {

    private InMemoryScoredOrganismRepository repository;
    private Organism mockOrganism1;
    private Organism mockOrganism2;
    private Organism mockOrganism3;

    @BeforeEach
    void setUp() {
        repository = new InMemoryScoredOrganismRepository();
        mockOrganism1 = new Organism("organism-1");
        mockOrganism2 = new Organism("organism-2");
        mockOrganism3 = new Organism("organism-3");
    }

    @Test
    @DisplayName("save() should generate ID when ScoredOrganism has no ID")
    void testSaveGeneratesId() {
        ScoredOrganism organismWithoutId = new ScoredOrganism(
                null, 10.0, "organism-1", mockOrganism1
        );

        ScoredOrganism saved = repository.save(organismWithoutId);

        assertNotNull(saved.id());
        assertFalse(saved.id().isEmpty());
        assertEquals(10.0, saved.score());
        assertEquals("organism-1", saved.organismId());
    }

    @Test
    @DisplayName("save() should preserve ID when ScoredOrganism has an ID")
    void testSavePreservesId() {
        ScoredOrganism organismWithId = new ScoredOrganism(
                "custom-id", 10.0, "organism-1", mockOrganism1
        );

        ScoredOrganism saved = repository.save(organismWithId);

        assertEquals("custom-id", saved.id());
        assertEquals(10.0, saved.score());
    }

    @Test
    @DisplayName("save() should maintain sorted order by score")
    void testSaveMaintainsSortedOrder() {
        ScoredOrganism low = new ScoredOrganism(5.0, mockOrganism1);
        ScoredOrganism high = new ScoredOrganism(15.0, mockOrganism2);
        ScoredOrganism medium = new ScoredOrganism(10.0, mockOrganism3);

        repository.save(low);
        repository.save(high);
        repository.save(medium);

        // Verify by checking that getRandomFromTopPercent returns lower scores (better)
        // and getRandomFromBottomPercent returns higher scores (worse)
        ScoredOrganism fromTop = repository.getRandomFromTopPercent(0.4f);
        ScoredOrganism fromBottom = repository.getRandomFromBottomPercent(0.4f);

        assertTrue(fromTop.score() <= fromBottom.score());
    }

    @Test
    @DisplayName("getById() should retrieve saved organism")
    void testGetById() {
        ScoredOrganism saved = repository.save(new ScoredOrganism(10.0, mockOrganism1));

        ScoredOrganism retrieved = repository.getById(saved.id());

        assertEquals(saved.id(), retrieved.id());
        assertEquals(saved.score(), retrieved.score());
        assertEquals(saved.organismId(), retrieved.organismId());
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

    @Test
    @DisplayName("delete() should remove organism from repository")
    void testDelete() {
        ScoredOrganism saved = repository.save(new ScoredOrganism(10.0, mockOrganism1));

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
        ScoredOrganism first = repository.save(new ScoredOrganism(10.0, mockOrganism1));
        ScoredOrganism second = repository.save(new ScoredOrganism(10.0, mockOrganism2));
        ScoredOrganism third = repository.save(new ScoredOrganism(10.0, mockOrganism3));

        repository.delete(second.id());

        assertThrows(IllegalArgumentException.class, () -> repository.getById(second.id()));
        assertDoesNotThrow(() -> repository.getById(first.id()));
        assertDoesNotThrow(() -> repository.getById(third.id()));
    }

    @Test
    @DisplayName("getRandomFromTopPercent() should return organism from top percentage")
    void testGetRandomFromTopPercent() {
        // Create 10 organisms with scores 1.0 to 10.0
        for (int i = 1; i <= 10; i++) {
            repository.save(new ScoredOrganism((double) i, new Organism("organism-" + i)));
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
        repository.save(new ScoredOrganism(10.0, mockOrganism1));

        assertThrows(IllegalArgumentException.class, () -> repository.getRandomFromTopPercent(-0.1f));
        assertThrows(IllegalArgumentException.class, () -> repository.getRandomFromTopPercent(1.5f));
    }

    @Test
    @DisplayName("getRandomFromBottomPercent() should return organism from bottom percentage")
    void testGetRandomFromBottomPercent() {
        // Create 10 organisms with scores 1.0 to 10.0
        for (int i = 1; i <= 10; i++) {
            repository.save(new ScoredOrganism((double) i, new Organism("organism-" + i)));
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
        repository.save(new ScoredOrganism(10.0, mockOrganism1));

        assertThrows(IllegalArgumentException.class, () -> repository.getRandomFromBottomPercent(-0.1f));
        assertThrows(IllegalArgumentException.class, () -> repository.getRandomFromBottomPercent(1.5f));
    }

    @Test
    @DisplayName("getRandomFromTopPercent() and getRandomFromBottomPercent() should have correct distribution")
    void testPercentageDistribution() {
        // Create 100 organisms with scores 1.0 to 100.0
        for (int i = 1; i <= 100; i++) {
            repository.save(new ScoredOrganism((double) i, new Organism("organism-" + i)));
        }

        // Test top 10% (best performers - lowest scores)
        Set<Double> topScores = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            ScoredOrganism top = repository.getRandomFromTopPercent(0.1f);
            topScores.add(top.score());
        }
        // All scores should be <= 10.0 (top 10% - best performers)
        assertTrue(topScores.stream().allMatch(score -> score <= 10.0));

        // Test bottom 10% (worst performers - highest scores)
        Set<Double> bottomScores = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            ScoredOrganism bottom = repository.getRandomFromBottomPercent(0.1f);
            bottomScores.add(bottom.score());
        }
        // All scores should be >= 91.0 (bottom 10% - worst performers)
        assertTrue(bottomScores.stream().allMatch(score -> score >= 91.0));
    }

    @Test
    @DisplayName("Repository should handle single organism correctly")
    void testSingleOrganism() {
        ScoredOrganism saved = repository.save(new ScoredOrganism(5.0, mockOrganism1));

        assertEquals(saved, repository.getRandomFromTopPercent(1.0f));
        assertEquals(saved, repository.getRandomFromBottomPercent(1.0f));
        assertEquals(saved, repository.getRandomFromTopPercent(0.5f));
        assertEquals(saved, repository.getRandomFromBottomPercent(0.5f));
    }

    @Test
    @DisplayName("Repository should handle multiple saves and deletes")
    void testMultipleSavesAndDeletes() {
        ScoredOrganism first = repository.save(new ScoredOrganism(1.0, mockOrganism1));
        ScoredOrganism second = repository.save(new ScoredOrganism(2.0, mockOrganism2));
        ScoredOrganism third = repository.save(new ScoredOrganism(3.0, mockOrganism3));

        repository.delete(second.id());

        assertThrows(IllegalArgumentException.class, () -> repository.getById(second.id()));
        assertDoesNotThrow(() -> repository.getById(first.id()));
        assertDoesNotThrow(() -> repository.getById(third.id()));

        // Verify ordering is maintained
        ScoredOrganism bottom = repository.getRandomFromBottomPercent(0.5f);
        assertTrue(bottom.score() == 1.0 || bottom.score() == 3.0);
    }

    @Test
    @DisplayName("Repository should handle extreme percentages")
    void testExtremePercentages() {
        for (int i = 1; i <= 10; i++) {
            repository.save(new ScoredOrganism((double) i, new Organism("organism-" + i)));
        }

        // Test 0% edge case - should still return an organism
        assertDoesNotThrow(() -> repository.getRandomFromTopPercent(0.0f));
        assertDoesNotThrow(() -> repository.getRandomFromBottomPercent(0.0f));

        // Test 100% - should be able to select from entire repository
        ScoredOrganism top100 = repository.getRandomFromTopPercent(1.0f);
        assertTrue(top100.score() >= 1.0 && top100.score() <= 10.0);

        ScoredOrganism bottom100 = repository.getRandomFromBottomPercent(1.0f);
        assertTrue(bottom100.score() >= 1.0 && bottom100.score() <= 10.0);
    }
}
