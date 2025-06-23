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
Retrieves a DataPoint at the specified index using modulo arithmetic to ensure safe array access. This method guarantees that a valid DataPoint is always returned as long as the DataQuantum contains at least one DataPoint.

**Index Behavior:**
- **Positive indices**: Standard array indexing (0, 1, 2, etc.)
- **Negative indices**: Reverse indexing where -1 returns the last DataPoint, -2 returns the second-to-last, etc.
- **Out-of-bounds indices**: Automatically wrapped using modulo operation to stay within valid range

**Examples:**
- For a DataQuantum with 3 DataPoints (indices 0, 1, 2):
  - `getDataPoint(0)` returns the first DataPoint
  - `getDataPoint(3)` returns the first DataPoint (3 % 3 = 0)
  - `getDataPoint(-1)` returns the last DataPoint
  - `getDataPoint(5)` returns the third DataPoint (5 % 3 = 2)

**Exception:** Throws `IllegalStateException` if the DataQuantum contains no DataPoints.

`void addValue(double value)`
Creates a new DataPoint with the provided value and a null sourceId, then adds it to the List.

`double getValue(int index)`
A convenience method for getDataPoint(index).getValue()

### DataQuantumConsumer

interface

#### Methods

`void consume(DataQuantum dataQuantum)` 

### Gene
Gene is a DataQuantumConsumer and implements the Mutational interface.

A TestGene, which concretely extends the Gene, is needed in the test code so that it can be referred to by GeneTest, ChromosomeTest, and OrganismTest.

Since Gene can have many different sub-classes, it needs to have a JsonTypeInfo annotation from the Jackson library that forces serialization and deserialization to use a fully-qualified class name for the type. Specifically, it should use @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type") to ensure proper polymorphic type handling.

#### Methods and properties

`String id`
The `id` property is accessible through a standard getter and setter.

`List<Integer> targetIndexList`
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

`abstract Gene copyOf()`
Every concrete Gene will need to implement the copyOf() method by first instantiating a new instance of the same type, and then calling `cloneProperties()` and passing in the new instance.

`boolean equals(Object obj)`
Gene overrides the java.lang.Object.equals() method.  Two Genes are equal if and only if their targetIndexList and operationalConstantList properties are also equal.  Concrete genes might further override the `equals()` method, but the id property should never be considered when determining if two Genes are equal.

`List<MutationCommand> getMutationCommandList()`
Implements the Mutational interface. Returns a list of possible mutations that can be applied to this Gene. The method uses helper methods to generate specific types of mutations:
- For each element in targetIndexList: calls `getTargetIndexUpMutationCommand()` and `getTargetIndexDownMutationCommand()` to create mutations that adjust target indices up or down by a random value between 1 and 5.
- For each element in operationConstantList: calls `getOperationalConstantUpMutationCommand()` and `getOperationalConstantDownMutationCommand()` to create mutations that adjust operational constants up or down by a random percentage between 1 and 20.

#### Helper Methods for Mutation Generation

`private MutationCommand getTargetIndexUpMutationCommand(Random random, int index)`
Creates a MutationCommand that increases the target index at the specified position by 1-5.

`private MutationCommand getTargetIndexDownMutationCommand(Random random, int index)`
Creates a MutationCommand that decreases the target index at the specified position by 1-5.

`private MutationCommand getOperationalConstantUpMutationCommand(Random random, int index)`
Creates a MutationCommand that increases the operational constant at the specified position by 1-20%.

`private MutationCommand getOperationalConstantDownMutationCommand(Random random, int index)`
Creates a MutationCommand that decreases the operational constant at the specified position by 1-20%.

### Chromosome
Chromosome is a set of Genes and implements the Mutational interface. Chromosomes can be cloned.

#### Methods and properties

`List<Gene> genes`
The genes property is exposed using standard getter and setter methods.

`void consume(DataQuantum dataQuantum)` 
Chromosome implements `consume()` by passing the DataQuantum to each of its Genes, in order.

`Chromosome copyOf()`
Returns a new instance of Chromosome. The genes property of the new Chromosome is created by calling clone() on each of the Genes in the original Chromosome.

`boolean equals(Object obj)`
Chromosome overrides the java.lang.Object.equals() method. Two Chromosomes are equal if and only if their genes properties are equal.

`List<MutationCommand> getMutationCommandList()`
Implements the Mutational interface. Returns a list of possible mutations that can be applied to this Chromosome. This includes mutations for the chromosome itself, as well as the MutationCommands for each of its Genes. The method uses helper methods to generate specific types of mutations:
- If genes list has more than one element: calls `getExchangeGeneMutationCommand()` to move a random Gene to a different place in the List.
- If genes list is not empty: calls `getRemoveRandomGeneMutationCommand()` to delete a random Gene.
- Always calls `getAddRandomGeneMutationCommand()` to add a random Gene using the GeneGenerator.
- Collects and includes all MutationCommands from each Gene in the chromosome.

#### Helper Methods for Mutation Generation

`private MutationCommand getExchangeGeneMutationCommand(Random random)`
Creates a MutationCommand that moves a random gene to a different position in the genes list.

`private MutationCommand getRemoveRandomGeneMutationCommand(Random random)`
Creates a MutationCommand that removes a randomly selected gene from the genes list.

`private MutationCommand getAddRandomGeneMutationCommand(Random random)`
Creates a MutationCommand that adds a randomly generated gene (using GeneGenerator) at a random position in the genes list.

### Organism
An Organism has an ordered list of Chromosomes and implements the Mutational interface.

An Organism is a DataQuantumConsumer. BaseOrganism implements consume() by passing dataQuantum to each of its Chromosomes, in order.

#### Methods and properties

`String id`
The `id` property is accessible through a standard getter and setter.

`List<Chromosome> chromosomes`
The `chromosomes` property is accessible through a standard getter and setter.

`void addChromosome(Chromosome chromosome)`
Adds chromosome to the end of the list of chromosomes.

`boolean equals(Object obj)`
Organism overrides the java.lang.Object.equals() method. Two Organisms are equal if and only if their chromosomes properties are equal. The id property is not part of equals comparison.

`List<MutationCommand> getMutationCommandList()`
Implements the Mutational interface. Returns a list of possible mutations that can be applied to this Organism. This includes mutations for the organism itself as well as all of the MutationCommands of its Chromosomes. The method uses helper methods to generate specific types of mutations:
- If chromosomes list has more than one element: calls `getExchangeChromosomeMutationCommand()` to reorder a single Chromosome and `getDeleteRandomChromosomeMutationCommand()` to delete a Chromosome.
- Always calls `getAddRandomChromosomeMutationCommand()` to add a random Chromosome using the ChromosomeGenerator.
- Collects and includes all MutationCommands from each Chromosome in the organism.

#### Helper Methods for Mutation Generation

`private MutationCommand getExchangeChromosomeMutationCommand(Random random)`
Creates a MutationCommand that moves a random chromosome to a different position in the chromosomes list.

`private MutationCommand getDeleteRandomChromosomeMutationCommand(Random random)`
Creates a MutationCommand that removes a randomly selected chromosome from the chromosomes list.

`private MutationCommand getAddRandomChromosomeMutationCommand(Random random)`
Creates a MutationCommand that adds a randomly generated chromosome (using ChromosomeGenerator) at a random position in the chromosomes list.

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