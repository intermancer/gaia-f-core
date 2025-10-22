package com.intermancer.gaiaf.core.evaluate;

/**
 * Repository interface for managing ScoredOrganism records.
 * Maintains organisms with their evaluation scores and provides
 * efficient access patterns for experimentation algorithms.
 */
public interface ScoredOrganismRepository {

    /**
     * Retrieves a ScoredOrganism record by its ID.
     *
     * @param id The unique identifier of the ScoredOrganism
     * @return The ScoredOrganism with the specified ID
     * @throws IllegalArgumentException if no ScoredOrganism with the given ID exists
     */
    ScoredOrganism getById(String id);

    /**
     * Saves a ScoredOrganism to the repository.
     * The scoredOrganism passed in does not need an ID; the returned
     * ScoredOrganism will have an ID populated by the repository.
     *
     * @param scoredOrganism The ScoredOrganism to save (without an ID)
     * @return The saved ScoredOrganism with an ID populated
     */
    ScoredOrganism save(ScoredOrganism scoredOrganism);

    /**
     * Deletes a ScoredOrganism from the repository by its ID.
     *
     * @param id The unique identifier of the ScoredOrganism to delete
     * @throws IllegalArgumentException if no ScoredOrganism with the given ID exists
     */
    void delete(String id);

    /**
     * Returns a random ScoredOrganism from the top percentage of scores.
     * For example, if percent is 0.1 (10%), a random organism from the
     * top 10% of scores will be returned.
     *
     * @param percent The percentage threshold (0.0 to 1.0) for selecting
     *                from the top-scoring organisms
     * @return A randomly selected ScoredOrganism from the top percentage
     * @throws IllegalArgumentException if the repository is empty or if
     *                                  percent is not between 0.0 and 1.0
     */
    ScoredOrganism getRandomFromTopPercent(float percent);

    /**
     * Returns a random ScoredOrganism from the bottom percentage of scores.
     * For example, if percent is 0.9 (90%), a random organism from the
     * bottom 90% of scores will be returned.
     *
     * @param percent The percentage threshold (0.0 to 1.0) for selecting
     *                from the bottom-scoring organisms
     * @return A randomly selected ScoredOrganism from the bottom percentage
     * @throws IllegalArgumentException if the repository is empty or if
     *                                  percent is not between 0.0 and 1.0
     */
    ScoredOrganism getRandomFromBottomPercent(float percent);
}