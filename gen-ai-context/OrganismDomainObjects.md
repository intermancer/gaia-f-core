# Organism Domain Objects

The domain objects described in this document are those that are necessary to fully define an Organism.

## Project details

The root project for Organism Domain Objects is com.intermancer.gaiaf.core.organism

## Domain Objects

### DataQuantum
A DataQuantum has an ordered list of values.  Values are doubles.

#### Methods

`void addValue(double value)` 
Adds a value to the DataQuantum

`double getValue(int index)` 
Uses mod on the index so that a value is always returned

### DataQuantumConsumer

interface

#### Methods

`void consume(DataQuantum dataQuantum)` 

### Gene
A Gene is a DataQuantumConsumer. Each Gene implements `consume()` by getting one or more values from the DataQuantum, operating on those values in some way, and then adding one or more values back into the DataQuantum.  Genes always add values to the DataQuantum.

### Chromosome
A Chromosome has an ordered list of Genes.

The default implementation of Chromosome is BaseChromosome.

A Chromosome is a DataQuantumConsumer.  BaseChromosome implements `consume()` by passing the DataQuantum to each of its Genes, in order.

### Organism
An Organism has an ordered list of Chromosomes.  

The default implementation of Organism is BaseOrganism.

An Organism is a DataQuantumConsumer. BaseOrganism implements consume() by passing dataQuantum to each of its Chromosomes, in order.
