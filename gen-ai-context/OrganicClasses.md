# Organism Domain Classes

The domain classes described in this document are those that are necessary to fully define an Organism.

## Project details

The root project for Organism Domain Classes is com.intermancer.gaiaf.core.organism

## Domain Classes

All of the concrete classes will need to be able to use Jackson for serialization and deserialization.  As a result, all concrete classes will need a no-argument constructor, and standard getters and setters for all properties.

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

### Gene
Gene is a DataQuantumConsumer.

A TestGene, which concretely extends the Gene, is needed in the test code so that it can be referred to by GeneTest, ChromosomeTest, and OrganismTest.

Since Gene can have many different sub-classes, it needs to have a JsonTypeInfo annotation from the Jackson library that forces serialization and deserialization to use a fully-qualified class name for the type. Specifically, it should use @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type") to ensure proper polymorphic type handling.

#### Methods and properties

`String id`

The `id` property is accessible through a standard getter and setter.

`List<Double> targetIndexList`

Gene has a targetIndexList property with a public getter that is a List of Integer initialized with a single value of -1.

`List<Double> operationConstantList`

Gene has an operationConstantList property, with a public getter and setter, that is a List of Double.  It is initialized to an empty List.

`abstract double[] operation(double[] values)`

Subclasses implement the `operation()` method to operate on the values from the DataQuantum.

`void consume(DataQuantum dataQuantum)` 

Gene implements a template method pattern for `void consume(DataQuantum dataQuantum)` that
  - uses the targetIndexList property to extract the values of the DataPoints in dataQuantum and create an array of primitive double.
  - passes the array of primitive double to the `operation()` method
  - for each of the values in the array that was returned by `operation()`
    - creates a new DataPoint using `new DataPoint(this.getId(), operation(dataPoint.getValue()))`
    - adds the new DataPoint to dataQuantum.

`void cloneProperties(Gene clone)`

Creates a new UUID for the clone Gene.

Creates a clone of targetIndexList by creating a new List<Double> and adding new instances of every element in targetIndexList. Calls clone.setTargetIndexList() with the clone.

Creates a clone of operationConstantList by creating a new List<Double> and adding new instances of every element in operationConstantList. Calls clone.setOperationConstantList() with the clone.

`abstract Gene clone()`

Every concrete Gene will need to implement the clone() method by first instantiating a new instance of the same type, and then calling `cloneProperties()` and passing in the new instance.

### Chromosome
Chromosome is a set of Genes.  Chromosomes can be cloned.

#### Methods and properties

`List<Gene> genes`
The genes property is exposed using standard getter and setter methods.

`void consume(DataQuantum dataQuantum)` 
Chromosome implements `consume()` by passing the DataQuantum to each of its Genes, in order.

`Chromosome clone()`
Returns a new instance of Chromosome. The genes property of the new Chromosome is created by calling clone() on each of the Genes in the original Chromosome.

### Organism
An Organism has an ordered list of Chromosomes.  

An Organism is a DataQuantumConsumer. BaseOrganism implements consume() by passing dataQuantum to each of its Chromosomes, in order.

#### Methods and properties

`String id`
The `id` property is accessible through a standard getter and setter.

`List<Chromosome> chromosomes`
The `chromosomes` property is accessible through a standard getter and setter.

`void addChromosome(Chromosome chromosome)`
Adds chromosome to the end of the list of chromosomes.

### OrganismBreeder

Interface for classes that breed two or more Organisms in order to generate descendents that are some sort of combination of the genetic material of their ancestors.

OrganismBreeder and its default concrete implementations are in the `breeding` package, which extends from the `organism` package.

#### Methods

`List<Organism> breed(List<Organism> parents)`
Returns a list of Organisms that have been generated based on some sort of combination of the parental organism that are provided as arguments.

### BasicOrganismBreeder

BasicBreeder functions like a genetic carousel, creating children by rotating parental chromosomes. Imagine parents standing in a circle, each holding their chromosomes in order. 

To create Child 1, take the first chromosome from Parent 1, the second from Parent 2, and so on—cycling back to Parent 1 when you run out of parents. For Child 2, shift one position: take the first chromosome from Parent 2, the second from Parent 3, etc. This pattern continues until you've created as many children as there were parents. 

Children are given a random ID with no set prefix or suffix.

## Organism Repository

The organism repository is a repository for organisms.

The repository package is under the organism package described above, with `repo` added.

### Classes and Interfaces

#### OrganismNotFoundException

Unchecked exception that is thrown when an Organism is not found in the repo.

#### OrganismRepository

Interface

##### Methods

`Organism getOrganismById(String organismId)` 
Retrieves an organism by its ID.  Throws OrganismNotFoundException if there is no organism with the given organismId in the Repo.

`Organism saveOrganism(Organism organism)` 
Saves or updates an organism.  Returns the Organism that was saved, with the id property set to the value that was set by the Repo.

`void deleteOrganism(String organismId)` 
Deletes an Organism by its ID.  Throws OrganismNotFoundException if there is no organism with the given organismId in the Repo.

`List<String> getAllOrganismIds()` 
Retrieves all ids of Organisms stored in the Repo.

#### InMemoryOrganismRepository

Implements all of the OrganismRepository methods using a simple HashMap.

Annotated with `@Component` so that it is automatically instantiated and wired in when the server starts.

##### Methods

`public OrganismRepository getInMemoryOrganismRepository()` 
Provides access to a Singleton instance of InMemoryOrganismRepository.  Annotated with `@Bean` to expose the Repo to the Spring ApplicationContext.

## Server Details

### OrganismController

The OrganismController is in the `controller` package that inherits from the base project package. It implements the endpoints described in this document.

Uses Spring Boot `@Autowire` to configure an OrganismRepository.

### Endpoints

#### /organism

`/organism` is the base context path for endpoints that do things with organisms.

#### /organism/repo

`/organism/repo` is the base context path for endpoints that interact with the organism repository is some way.

#### GET /organism/repo/list

returns a List of all Organism IDs in the repository.

#### GET /organism/repo/{organismId}

`/organism/repo/{organismId}` returns the representation of an organism with the given ID. If the repository does not contain such an entity, then it will return a 404.

#### DELETE /organism/repo/{organismId}

Deletes the organism with the provided ID from the repository.  If the organism does not exist, then it will return a 404.

#### POST /organism/repo

Assumes that the message contains a JSON representation of an organism and adds it to the repository.