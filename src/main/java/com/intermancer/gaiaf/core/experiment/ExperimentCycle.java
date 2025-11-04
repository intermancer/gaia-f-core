
package com.intermancer.gaiaf.core.experiment;

import com.intermancer.gaiaf.core.evaluate.ScoredOrganism;
import com.intermancer.gaiaf.core.organism.Organism;

import java.util.List;

/**
 * The Experiment interface defines the Experiment Life Cycle phases.
 * Implementations delegate to injected objects to perform the actual work.
 */
public interface ExperimentCycle {

    /**
     * Executes a complete mutation cycle including parent selection, breeding,
     * mutation, evaluation, and repository maintenance.
     */
    void mutationCycle();

    /**
     * Selects parent organisms for breeding.
     * Default algorithm chooses one parent from the top 10% and one from the bottom 90%.
     *
     * @return list of selected parent organisms with their scores
     */
    List<ScoredOrganism> selectParents();

    /**
     * Breeds the selected parents to generate child organisms.
     *
     * @param parents the parent organisms to breed
     * @return list of child organisms
     */
    List<Organism> breedParents(List<Organism> parents);

    /**
     * Mutates the child organisms to introduce new behavior.
     *
     * @param children the child organisms to mutate
     */
    void mutateChildren(List<Organism> children);

    /**
     * Evaluates the child organisms and returns them with their scores.
     *
     * @param children the child organisms to evaluate
     * @return list of evaluated children with their scores
     */
    List<ScoredOrganism> evaluateChildren(List<Organism> children);

    /**
     * Maintains the repository by potentially replacing parents with better-performing children.
     *
     * @param parents the parent organisms with their scores
     * @param children the child organisms with their scores
     */
    void maintainRepository(List<ScoredOrganism> parents, List<ScoredOrganism> children);
}