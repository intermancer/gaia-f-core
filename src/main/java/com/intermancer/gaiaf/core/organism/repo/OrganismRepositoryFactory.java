package com.intermancer.gaiaf.core.organism.repo;

public class OrganismRepositoryFactory {
    private static final InMemoryOrganismRepository INSTANCE = new InMemoryOrganismRepository();

    public static OrganismRepository getInstance() {
        return INSTANCE;
    }
}