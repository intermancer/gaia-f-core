package com.intermancer.gaiaf.core.evaluate;

import com.intermancer.gaiaf.core.organism.Organism;

/**
 * Interface for classes that grade organisms, giving them a numerical score.
 * By convention, a score closer to 0 is better, as 0 can be interpreted as 
 * "no deviations between predicted and actual values".
 */
public interface Evaluator {
    
    /**
     * Evaluate the given Organism.
     * 
     * @param organism The organism to evaluate
     * @return A numerical score where lower values indicate better performance,
     *         with 0 representing perfect prediction accuracy
     */
    double evaluate(Organism organism);
}