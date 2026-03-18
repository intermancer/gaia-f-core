package com.intermancer.gaiaf.core.evaluate;

import java.util.List;
import java.util.Set;

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
     * Since lower scores indicate better performance (closer to 0), the top
     * percentage refers to organisms with the lowest scores.
     * For example, if the percent is 0.1 (10%), a random organism from the
     * best-performing 10% (lowest scores) will be returned.
     *
     * @param percent The percentage threshold (0.0 to 1.0) for selecting
     *                from the top-scoring organisms
     * @return A randomly selected ScoredOrganism from the top percentage
     * @throws IllegalArgumentException if the repository is empty, or if
     *                                  percent is not between 0.0 and 1.0
     */
    ScoredOrganism getRandomFromTopPercent(float percent);

    /**
     * Returns a random ScoredOrganism from the bottom percentage of scores.
     * Since lower scores indicate better performance (closer to 0), the bottom
     * percentage refers to organisms with the highest scores.
     * For example, if the percent is 0.9 (90%), a random organism from the
     * worst-performing 90% (highest scores) will be returned.
     *
     * @param percent The percentage threshold (0.0 to 1.0) for selecting
     *                from the bottom-scoring organisms
     * @return A randomly selected ScoredOrganism from the bottom percentage
     * @throws IllegalArgumentException if the repository is empty, or if
     *                                  percent is not between 0.0 and 1.0
     */
    ScoredOrganism getRandomFromBottomPercent(float percent);

    /**
     * Returns the current number of ScoredOrganisms in the repository.
     *
     * @return The current size of the repository
     */
    int size();

    /**
     * Returns the current number of ScoredOrganisms in the repository for the given experimentId.
     *
     * @param experimentId The ID of the experiment to count organisms for
     * @return The current size of the repository for the given experimentId
     */
    int size(String experimentId);

    /**
     * Returns a list of all ScoredOrganism IDs currently stored in the repository 
     * with the given experimentId.
     * The list contains the id property from each ScoredOrganism, not the organismId property.
     *
     * @param experimentId The ID of the experiment to retrieve organism IDs for
     * @return A list of all ScoredOrganism IDs in the repository with the given experimentId
     */
    List<String> getAllOrganismIds(String experimentId);

    /**
     * Returns a random ScoredOrganism from the top percentage of scores with the given experimentId.
     * Since lower scores indicate better performance (closer to 0), the top
     * percentage refers to organisms with the lowest scores.
     * For example, if the percent is 0.1 (10%), a random organism from the
     * best-performing 10% (lowest scores) will be returned.
     *
     * @param experimentId The ID of the experiment to select from
     * @param percent The percentage threshold (0.0 to 1.0) for selecting
     *                from the top-scoring organisms
     * @return A randomly selected ScoredOrganism from the top percentage
     * @throws IllegalArgumentException if the repository is empty, or if
     *                                  percent is not between 0.0 and 1.0
     */
    ScoredOrganism getRandomFromTopPercent(String experimentId, float percent);

    /**
     * Returns a random ScoredOrganism from the bottom percentage of scores with the given experimentId.
     * Since lower scores indicate better performance (closer to 0), the bottom
     * percentage refers to organisms with the highest scores.
     * For example, if the percent is 0.9 (90%), a random organism from the
     * worst-performing 90% (highest scores) will be returned.
     *
     * @param experimentId The ID of the experiment to select from
     * @param percent The percentage threshold (0.0 to 1.0) for selecting
     *                from the bottom-scoring organisms
     * @return A randomly selected ScoredOrganism from the bottom percentage
     * @throws IllegalArgumentException if the repository is empty, or if
     *                                  percent is not between 0.0 and 1.0
     */
    ScoredOrganism getRandomFromBottomPercent(String experimentId, float percent);

    /**
     * Returns a paginated list of ScoredOrganisms for the given experiment,
     * sorted by score ascending (best scores first).
     *
     * @param experimentId The ID of the experiment to retrieve organisms for
     * @param offset The starting index (0-based)
     * @param limit The maximum number of results to return
     * @return A list of ScoredOrganisms sorted by score
     */
    List<ScoredOrganism> getScoredOrganismsByExperiment(String experimentId, int offset, int limit);

    /**
     * Returns a set of all unique experiment IDs that have scored organisms
     * in the repository.
     *
     * @return A set of experiment IDs
     */
    Set<String> getAllExperimentIds();
}