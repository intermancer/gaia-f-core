# Organism Domain Objects

The domain objects described in this document are those that are necessary to fully define an Organism.

## Project details

The root project for Organism Domain Objects is com.intermancer.gaiaf.core.organism

## Domain Objects

### DataQuantum
A DataQuantum has an ordered list of DataPoints.  

**DataPoint** is a public inner class of DataQuantum. A DataPoint has the following properties with getters and setters:
| proper name | type |
|--|--|
| sourceId | string |
| value | double |

sourceId can be null.

#### Methods

`void addDataPoint(DataPoint dataPoint)` 
Adds a DataPoint to the DataQuantum.  Does not allow nulls.

`DataPoint getDataPoint(int index)` 
Uses mod on the index so that the call allways returns an object.

`void addValue(double value)`
Creates a new DataPoint with the provided value and a null sourceId, then adds it to the List.

`double getValue(int index)`
A convenience method for getDataPoint(index).getValue

### DataQuantumConsumer

interface

#### Methods

`void consume(DataQuantum dataQuantum)` 
`String getId()`

### Gene
Gene is a DataQuantumConsumer.

Gene has a targetIndexList property with a public getter that is a List of Integer initialized with a single value of -1.

Gene has an operationConstantList property, with a public getter and setter, that is a List of Double.  It is initialized to an empty List.

Gene has a public setter for the id property, in addition to the getter that was declared in DataQuantumConsumer.

Gene declares an abstract method `double[] operation(double[] values)`. Subclasses implement the `operation()` method to operate on the values from the DataQuantum.

Gene implements a template method pattern for `void consume(DataQuantum dataQuantum)` that
  - uses the targetIndexList property to extract the values of the DataPoints in dataQuantum and create an array of primitive double.
  - passes the array of primitive double to the `operation()` method
  - for each of the values in the array that was returned by `operation()`
    - creates a new DataPoint using `new DataPoint(this.getId(), operation(dataPoint.getValue()))`
    - adds the new DataPoint to dataQuantum.

A TestGene, which concretely extends the Gene, is needed in the test code so that it can be referred to by GeneTest, ChromosomeTest, and OrganismTest.

### Chromosome
A Chromosome has an ordered list of Genes.

A Chromosome is a DataQuantumConsumer. Chromosome implements `consume()` by passing the DataQuantum to each of its Genes, in order.

### Organism
An Organism has an ordered list of Chromosomes.  

The default implementation of Organism is BaseOrganism.

An Organism is a DataQuantumConsumer. BaseOrganism implements consume() by passing dataQuantum to each of its Chromosomes, in order.
