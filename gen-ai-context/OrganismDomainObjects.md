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
A Chromosome has an ordered list of Genes. The genes property is exposed using standard getter and setter methods.

A Chromosome is a DataQuantumConsumer. Chromosome implements `consume()` by passing the DataQuantum to each of its Genes, in order.

### Organism
An Organism has an ordered list of Chromosomes.  

An Organism is a DataQuantumConsumer. BaseOrganism implements consume() by passing dataQuantum to each of its Chromosomes, in order.

#### Methods

`void addChromosome(Chromosome chromosome)`
Adds chromosome to the end of the list of chromosomes.

`List<Chromosome> getChromosomes()`
Returns the list of chromosomes.

## Organism Repository

The organism repository is a repository for organisms.

The repository package is under the organism package described above, with `repo` added.

### Classes and Interfaces

#### OrganismNotFoundException

Unchecked exception that is thrown when an Organism is not found in the repo.

#### OrganismRepository

Interface

##### Methods

`Organism getOrganismById(String organismId)` Retrieves an organism by its ID.  Throws OrganismNotFoundException if there is no organism with the given organismId in the Repo.

`Organism saveOrganism(Organism organism)` Saves or updates an organism.  Returns the Organism that was saved, with the id property set to the value that was set by the Repo.

`void deleteOrganism(String organismId)` Deletes an Organism by its ID.  Throws OrganismNotFoundException if there is no organism with the given organismId in the Repo.

`List<String> getAllOrganismIds()` Retrieves all ids of Organisms stored in the Repo.

#### InMemoryOrganismRepository

Implements all of the OrganismRepository methods using a simple HashMap.

#### OrganismRepositoryFactory

Annotated with `@Configuration` to mark it as a Spring Boot configuration class.

##### Methods

`public OrganismRepository getInMemoryOrganismRepository()` Provides access to a Singleton instance of InMemoryOrganismRepository.  Annotated with `@Bean` to expose the Repo to the Spring ApplicationContext.

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

`/organism/repo{organismId}` returns the representation of an organism with the given ID. If the repository does not contain such an entity, then it will return a 404.

#### DELETE /organism/repo/{organismId}

Deletes the organism with the provided ID from the repository.  If the organism does not exist, then it will return a 404.

#### POST /organism/repo

Assumes that the message contains a JSON representation of an organism and adds it to the repository.