package com.intermancer.gaiaf.core.organism.repo;

public class OrganismNotFoundException extends RuntimeException {
    public OrganismNotFoundException(String message) {
        super(message);
    }
}