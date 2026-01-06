package com.intermancer.gaiaf.core.evaluate;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An in-memory implementation of the ScoredOrganismRepository interface.
 * For each experimentId, maintains a ranked List of ScoredOrganisms for efficient 
 * score-based searches, as well as a Map of ScoredOrganisms (the "identity Map") 
 * for lookups by ID. Uses a Map of Lists (the "ordered Map") to maintain the order.
 */
@Repository
public class InMemoryScoredOrganismRepository implements ScoredOrganismRepository {

    // Identity Map: for fast ID-based lookups
    private final Map<String, ScoredOrganism> organismMap = new ConcurrentHashMap<>();
    
    // Ordered Map: Maps experimentId -> ranked list of ScoredOrganisms
    private final Map<String, List<ScoredOrganism>> orderedMap = new ConcurrentHashMap<>();

    @Override
    public ScoredOrganism getById(String id) {
        ScoredOrganism organism = organismMap.get(id);
        if (organism == null) {
            throw new IllegalArgumentException("No ScoredOrganism found with ID: " + id);
        }
        return organism;
    }

    @Override
    public ScoredOrganism save(ScoredOrganism scoredOrganism) {
        // Generate ID if not present
        ScoredOrganism organismToSave;
        if (scoredOrganism.id() == null || scoredOrganism.id().isEmpty()) {
            organismToSave = new ScoredOrganism(
                UUID.randomUUID().toString(),
                scoredOrganism.score(),
                scoredOrganism.organismId(),
                scoredOrganism.organism(),
                scoredOrganism.experimentId()
            );
        } else {
            organismToSave = scoredOrganism;
        }

        String experimentId = organismToSave.experimentId();
        
        // Add to identity map
        organismMap.put(organismToSave.id(), organismToSave);

        // Get or create the ranked list for this experiment
        List<ScoredOrganism> rankedList = orderedMap.computeIfAbsent(
            experimentId, 
            k -> Collections.synchronizedList(new ArrayList<>())
        );

        synchronized (rankedList) {
            // Find insertion point using binary search
            int insertionPoint = Collections.binarySearch(rankedList, organismToSave);
            if (insertionPoint < 0) {
                // Convert insertion point to actual index
                insertionPoint = -(insertionPoint + 1);
            }
            rankedList.add(insertionPoint, organismToSave);
        }

        return organismToSave;
    }

    @Override
    public void delete(String id) {
        // Look up the organism from the map
        ScoredOrganism organism = organismMap.get(id);
        if (organism == null) {
            throw new IllegalArgumentException("No ScoredOrganism found with ID: " + id);
        }

        String experimentId = organism.experimentId();
        List<ScoredOrganism> rankedList = orderedMap.get(experimentId);
        
        if (rankedList == null) {
            throw new IllegalArgumentException("No ranked list found for experimentId: " + experimentId);
        }

        synchronized (rankedList) {
            // Remove from identity map
            organismMap.remove(id);

            // Find and remove from ranked list using binary search by score
            int index = Collections.binarySearch(rankedList, organism);
            if (index >= 0) {
                // Binary search may find any element with the same score
                // We need to find the exact element by ID
                while (index < rankedList.size() && 
                       rankedList.get(index).score().equals(organism.score())) {
                    if (rankedList.get(index).id().equals(id)) {
                        rankedList.remove(index);
                        return;
                    }
                    index++;
                }
                // Search backwards as well
                index = Collections.binarySearch(rankedList, organism);
                while (index >= 0 && 
                       rankedList.get(index).score().equals(organism.score())) {
                    if (rankedList.get(index).id().equals(id)) {
                        rankedList.remove(index);
                        return;
                    }
                    index--;
                }
            }
        }
    }

    @Override
    public ScoredOrganism getRandomFromTopPercent(float percent) {
        if (percent < 0.0f || percent > 1.0f) {
            throw new IllegalArgumentException("Percent must be between 0.0 and 1.0, got: " + percent);
        }

        if (organismMap.isEmpty()) {
            throw new IllegalArgumentException("Repository is empty");
        }

        // Create a combined sorted list from all experiments
        List<ScoredOrganism> allOrganisms = new ArrayList<>(organismMap.values());
        allOrganisms.sort(Comparator.naturalOrder());

        // Calculate the cutoff index for the top percent
        int size = allOrganisms.size();
        int cutoffIndex = (int) Math.ceil(size * percent);
        if (cutoffIndex > size) {
            cutoffIndex = size;
        }
        if (cutoffIndex == 0) {
            cutoffIndex = 1;
        }

        // Select a random organism from the top percentage
        Random random = new Random();
        int randomIndex = random.nextInt(cutoffIndex);
        return allOrganisms.get(randomIndex);
    }

    @Override
    public ScoredOrganism getRandomFromBottomPercent(float percent) {
        if (percent < 0.0f || percent > 1.0f) {
            throw new IllegalArgumentException("Percent must be between 0.0 and 1.0, got: " + percent);
        }

        if (organismMap.isEmpty()) {
            throw new IllegalArgumentException("Repository is empty");
        }

        // Create a combined sorted list from all experiments
        List<ScoredOrganism> allOrganisms = new ArrayList<>(organismMap.values());
        allOrganisms.sort(Comparator.naturalOrder());

        // Calculate the cutoff index for the bottom percent
        int size = allOrganisms.size();
        int cutoffIndex = (int) Math.ceil(size * (1.0 - percent));
        if (cutoffIndex >= size) {
            cutoffIndex = size - 1;
        }

        // Select a random organism from the bottom percentage
        Random random = new Random();
        int randomIndex = cutoffIndex + random.nextInt(size - cutoffIndex);
        return allOrganisms.get(randomIndex);
    }

    @Override
    public int size() {
        return organismMap.size();
    }

    @Override
    public int size(String experimentId) {
        List<ScoredOrganism> rankedList = orderedMap.get(experimentId);
        return rankedList == null ? 0 : rankedList.size();
    }

    @Override
    public List<String> getAllOrganismIds(String experimentId) {
        List<ScoredOrganism> rankedList = orderedMap.get(experimentId);
        if (rankedList == null) {
            return Collections.emptyList();
        }
        
        synchronized (rankedList) {
            return rankedList.stream()
                    .map(ScoredOrganism::id)
                    .toList();
        }
    }

    @Override
    public ScoredOrganism getRandomFromTopPercent(String experimentId, float percent) {
        if (percent < 0.0f || percent > 1.0f) {
            throw new IllegalArgumentException("Percent must be between 0.0 and 1.0, got: " + percent);
        }

        List<ScoredOrganism> rankedList = orderedMap.get(experimentId);
        if (rankedList == null || rankedList.isEmpty()) {
            throw new IllegalArgumentException("Repository is empty for experimentId: " + experimentId);
        }

        synchronized (rankedList) {
            // Calculate the cutoff index for the top percent
            // Since the list is sorted in ascending order and lower scores are better,
            // top scores (best performers) are at the beginning
            int size = rankedList.size();
            int cutoffIndex = (int) Math.ceil(size * percent);
            if (cutoffIndex > size) {
                cutoffIndex = size;
            }
            if (cutoffIndex == 0) {
                cutoffIndex = 1;
            }

            // Select a random organism from the top percentage
            Random random = new Random();
            int randomIndex = random.nextInt(cutoffIndex);
            return rankedList.get(randomIndex);
        }
    }

    @Override
    public ScoredOrganism getRandomFromBottomPercent(String experimentId, float percent) {
        if (percent < 0.0f || percent > 1.0f) {
            throw new IllegalArgumentException("Percent must be between 0.0 and 1.0, got: " + percent);
        }

        List<ScoredOrganism> rankedList = orderedMap.get(experimentId);
        if (rankedList == null || rankedList.isEmpty()) {
            throw new IllegalArgumentException("Repository is empty for experimentId: " + experimentId);
        }

        synchronized (rankedList) {
            // Calculate the cutoff index for the bottom percent
            // Since the list is sorted in ascending order and lower scores are better,
            // bottom scores (worst performers) are at the end
            int size = rankedList.size();
            int cutoffIndex = (int) Math.ceil(size * (1.0 - percent));
            if (cutoffIndex >= size) {
                cutoffIndex = size - 1;
            }

            // Select a random organism from the bottom percentage
            Random random = new Random();
            int randomIndex = cutoffIndex + random.nextInt(size - cutoffIndex);
            return rankedList.get(randomIndex);
        }
    }
}
