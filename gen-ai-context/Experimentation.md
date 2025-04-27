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

The BasicSeeder is a very basic implementation of the Seeder interface. It simply and staticly defines 5 Organisms and loads them into the repo.

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