package com.intermancer.gaiaf.core.organism.repo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrganismRepositoryFactory {
    private static final InMemoryOrganismRepository INSTANCE = new InMemoryOrganismRepository();

    @Bean
    public OrganismRepository getInMemoryOrganismRepository() {
        return INSTANCE;
    }
}