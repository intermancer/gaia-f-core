package com.intermancer.gaiaf.core.experiment;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Interface for genetic elements that can be mutated.
 */
public interface Mutational {
    /**
     * Returns a list of possible mutations that can be applied to this genetic element.
     * This method is annotated with @JsonIgnore to exclude it from JSON serialization.
     * 
     * @return List of MutationCommand objects representing possible mutations
     */
    @JsonIgnore
    List<MutationCommand> getMutationCommandList();
}