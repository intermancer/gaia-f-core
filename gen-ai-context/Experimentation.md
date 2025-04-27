# Experimentation

This document describes the Objects and changes to existing Objects to enable experimentation.

## Experiment Life Cycle

### Seeding

Seeding is the first step in Experimentation.  An OrganismRepository is initialized with a set of seed Organisms.

## Objects

The package for the experimentation objects extends from the root package of the project with /experiment

### Seeder

Interface

Concrete implementations of the Seeder interface will have an OrganismRepository autowired in.

#### Methods

`seed()` 
The seed method of a concrete Seeder will load Organisms into the injecte OrganismRepository

### BasicSeeder

The BasicSeeder is a very basic implementation of the Seeder interface. It simply and staticly defines 5 Organisms and loads them into the repo