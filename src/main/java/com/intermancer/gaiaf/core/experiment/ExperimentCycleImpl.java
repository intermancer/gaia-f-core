package com.intermancer.gaiaf.core.experiment;

import com.intermancer.gaiaf.core.evaluate.Evaluator;
import com.intermancer.gaiaf.core.evaluate.ScoredOrganism;
import com.intermancer.gaiaf.core.evaluate.ScoredOrganismRepository;
import com.intermancer.gaiaf.core.organism.Organism;
import com.intermancer.gaiaf.core.organism.breeding.OrganismBreeder;
import com.intermancer.gaiaf.core.organism.repo.OrganismRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Default implementation of the Experiment interface.
 * Implements the Experiment Life Cycle phases by delegating to injected objects.
 */
@Component
public class ExperimentCycleImpl implements ExperimentCycle {
    
    private final OrganismRepository organismRepository;
    private final ScoredOrganismRepository scoredOrganismRepository;
    private final OrganismBreeder organismBreeder;
    private final Evaluator evaluator;
    private final ExperimentConfiguration experimentConfiguration;
    private final Random random;
    
    @Autowired
    public ExperimentCycleImpl(
            OrganismRepository organismRepository,
            ScoredOrganismRepository scoredOrganismRepository,
            OrganismBreeder organismBreeder,
            Evaluator evaluator,
            ExperimentConfiguration experimentConfiguration) {
        this.organismRepository = organismRepository;
        this.scoredOrganismRepository = scoredOrganismRepository;
        this.organismBreeder = organismBreeder;
        this.evaluator = evaluator;
        this.experimentConfiguration = experimentConfiguration;
        this.random = new Random();
    }
    
    /**
     * Executes a complete mutation cycle including parent selection, breeding,
     * mutation, evaluation, and repository maintenance.
     */
    @Override
    public void mutationCycle() {
        List<ScoredOrganism> parents = selectParents();
        List<Organism> parentOrganisms = parents.stream()
                .map(ScoredOrganism::organism)
                .toList();
        List<Organism> children = breedParents(parentOrganisms);
        mutateChildren(children);
        List<ScoredOrganism> scoredChildren = evaluateChildren(children);
        maintainRepository(parents, scoredChildren);
    }
    
    /**
     * Selects parent organisms for breeding.
     * Chooses one parent from the top 10% and one from the bottom 90%.
     * 
     * @return list of selected parent organisms with their scores
     */
    @Override
    public List<ScoredOrganism> selectParents() {
        List<ScoredOrganism> parents = new ArrayList<>();
        parents.add(scoredOrganismRepository.getRandomFromTopPercent(0.1f));
        parents.add(scoredOrganismRepository.getRandomFromBottomPercent(0.9f));
        return parents;
    }
    
    /**
     * Breeds the selected parents to generate child organisms.
     * Uses the injected OrganismBreeder to generate a list of child Organisms.
     * 
     * @param parents the parent organisms to breed
     * @return list of child organisms
     */
    @Override
    public List<Organism> breedParents(List<Organism> parents) {
        return organismBreeder.breed(parents);
    }
    
    /**
     * Mutates the child organisms to introduce new behavior.
     * Mutates each child a random number (between 1 and 5) of times.
     * 
     * @param children the child organisms to mutate
     */
    @Override
    public void mutateChildren(List<Organism> children) {
        for (Organism child : children) {
            if (child instanceof Mutational mutational) {
                int mutationCount = random.nextInt(5) + 1; // Random number between 1 and 5
                for (int i = 0; i < mutationCount; i++) {
                    List<MutationCommand> mutations = mutational.getMutationCommandList();
                    if (!mutations.isEmpty()) {
                        MutationCommand mutation = mutations.get(random.nextInt(mutations.size()));
                        mutation.execute();
                    }
                }
            }
        }
    }
    
    /**
     * Evaluates the child organisms and returns them with their scores.
     * Uses the injected Evaluator to evaluate the child organisms.
     * 
     * @param children the child organisms to evaluate
     * @return list of evaluated children with their scores
     */
    @Override
    public List<ScoredOrganism> evaluateChildren(List<Organism> children) {
        List<ScoredOrganism> scoredChildren = new ArrayList<>();
        for (Organism child : children) {
            double score = evaluator.evaluate(child);
            ScoredOrganism scoredChild = new ScoredOrganism(null, score, child.getId(), child);
            scoredChildren.add(scoredChild);
        }
        return scoredChildren;
    }

    /**
     * Maintains the repository by potentially replacing parents with better-performing children.
     * If the repository is not at capacity, simply adds the children to the repository.
     * If the repository is at capacity, combines all family members (parents and children) 
     * into a single list and sorts by score.
     * If the top two organisms are the parents, no changes are made (preserves best parent).
     * If one of the top two is a child, the worst parent is replaced.
     * If both top two are children, both parents are replaced.
     *
     * @param parents the parent organisms with their scores
     * @param children the child organisms with their scores
     */
    @Override
    public void maintainRepository(List<ScoredOrganism> parents, List<ScoredOrganism> children) {
        if (parents.size() != 2 || children.isEmpty()) {
            return;
        }

        // Check if repository is at capacity
        int currentSize = scoredOrganismRepository.size();
        int capacity = experimentConfiguration.getRepoCapacity();

        // If not at capacity, simply add all children
        if (currentSize < capacity) {
            for (ScoredOrganism child : children) {
                Organism savedOrganism = organismRepository.saveOrganism(child.organism());
                ScoredOrganism savedScoredChild = new ScoredOrganism(
                        null, child.score(), savedOrganism.getId(), savedOrganism);
                scoredOrganismRepository.save(savedScoredChild);
            }
            return;
        }

        // Repository is at capacity - use comparison algorithm

        // 1. Add parents and children to a single list and sort
        List<ScoredOrganism> family = new ArrayList<>();
        family.addAll(parents);
        family.addAll(children);
        family.sort(ScoredOrganism::compareTo);

        ScoredOrganism topFirst = family.get(0);
        ScoredOrganism topSecond = family.get(1);

        // 2. If the top two organisms are the parents, return
        if (parents.contains(topFirst) && parents.contains(topSecond)) {
            return;
        }

        // Identify which parent is worst (will be removed if either child makes top 2)
        ScoredOrganism worstParent = parents.get(0).compareTo(parents.get(1)) < 0
                ? parents.get(1) : parents.get(0);

        // 3. If one of the top two organisms is a child, delete the worst parent and add the child
        if ((parents.contains(topFirst) && children.contains(topSecond)) ||
                (children.contains(topFirst) && parents.contains(topSecond))) {

            ScoredOrganism childToAdd = children.contains(topFirst) ? topFirst : topSecond;

            scoredOrganismRepository.delete(worstParent.id());
            organismRepository.deleteOrganism(worstParent.organismId());

            Organism savedOrganism = organismRepository.saveOrganism(childToAdd.organism());
            ScoredOrganism savedScoredChild = new ScoredOrganism(
                    null, childToAdd.score(), savedOrganism.getId(), savedOrganism);
            scoredOrganismRepository.save(savedScoredChild);
        }
        // 4. If both of the top two organisms are children, delete both parents and add both children
        else if (children.contains(topFirst) && children.contains(topSecond)) {
            // Delete both parents
            for (ScoredOrganism parent : parents) {
                scoredOrganismRepository.delete(parent.id());
                organismRepository.deleteOrganism(parent.organismId());
            }

            // Add both children
            for (ScoredOrganism child : List.of(topFirst, topSecond)) {
                Organism savedOrganism = organismRepository.saveOrganism(child.organism());
                ScoredOrganism savedScoredChild = new ScoredOrganism(
                        null, child.score(), savedOrganism.getId(), savedOrganism);
                scoredOrganismRepository.save(savedScoredChild);
            }
        }
    }
}
