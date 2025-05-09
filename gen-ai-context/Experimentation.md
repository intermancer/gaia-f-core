# Experimentation

This document describes the Classes and changes to existing Classes to enable experimentation.

## Experiment Life Cycle

### Seed

Seed is the first step in Experimentation.  An OrganismRepository is initialized with a set of seed Organisms.

## Classes to support the Experiment Life Cycle

The package for the experimentation classes extends from the root package of the project with /experiment

### Seeder

Interface

#### Methods

`void seed(OrganismRepository repo)` 
The seed method of a concrete Seeder will load Organisms into the provided repo.

### BasicSeeder

The BasicSeeder is a very basic implementation of the Seeder interface. It simply and staticly defines 5 Organisms and loads them into the repo.

BasicSeeder uses the `@Component` annotation to make itself available to other classes.

#### Organisms

##### Simple Arithmetic Organism

Chromosome 1: AdditionGene → MultiplicationGene
This organism first adds 1.5 to an input value, then multiplies by 1.5
Useful for demonstrating basic sequential gene operations

##### Trigonometric Analysis Organism

Chromosome 1: SineGene → MultiplicationGene
This organism applies sine function and then amplifies the result by 1.5
Demonstrates combination of trigonometric and arithmetic operations

##### Data Transformation Organism

Chromosome 1: AdditionGene → SubtractionGene
Chromosome 2: MultiplicationGene → DivisionGene
Two parallel chromosomes: one for additive operations, another for multiplicative
Shows how multiple chromosomes can process data differently

##### Reductive Processing Organism

Chromosome 1: DivisionGene → SubtractionGene → SineGene
A 3-gene chromosome that reduces values, normalizes, and applies trigonometry
Demonstrates longer processing chains

##### Basic Composite Organism

Chromosome 1: MultiplicationGene
Chromosome 2: AdditionGene → SineGene
Chromosome 3: SubtractionGene
Simple organism with 3 chromosomes showing different gene combinations
Useful for testing organism-level data flow

### Experiment

The Experiment class implements the Experiment Life Cycle phases, mostly by delegating to injected objects.

#### Autowired dependencies

The Experiment depends on Autowired instances of Seeder and OrganismRepository.

#### Methods

`void seed()`
Calls the `seed()` method of the injected Seeder, passing in the injected OrganismRepository.

## Server Details

### ExperimentController

The ExperimentController is in the `controller` package that inherits from the base project package. It implements the endpoints described in this document.

The ExperimentController autowires an OrganismRepository and an E
Uses Spring Boot `@Autowire` to configure an OrganismRepository.

### Endpoints

#### /experiment

`/experiment` is the base context path for endpoints that manage the Experiment Life Cycle.

#### GET /experiment/seed

Seeds the OrganismRepository with the Organisms created by the Seeder and returns all of the Organism IDs.