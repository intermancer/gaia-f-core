package com.intermancer.gaiaf.core.experiment;

import java.util.List;

/**
 * Interface for genetic elements that can be mutated.
 */
public interface Mutational {
    /**
     * Returns a list of possible mutations that can be applied to this genetic element.
     * 
     * @return List of MutationCommand objects representing possible mutations
     */
    List<MutationCommand> getMutationCommandList();
}