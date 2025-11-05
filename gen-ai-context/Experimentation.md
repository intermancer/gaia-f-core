# Experimentation

This document describes the Classes and changes to existing Classes to enable experimentation.

## Experiment Coordination

An Experiment is composed of seeding a repository with Scored Organisms, and then running a number of Experiment Cycles.

### Experiment

Interface.  An Experiment is responsible for orchestrating the complete experimentation process.

### BasicExperimentImpl

BasicExperimentImpl uses the `@Component` annotation to make itself available for dependency injection.

#### Autowired Dependencies

The BasicExperimentImpl depends on:
- `Seeder` - for initializing the repositories with evaluated seed organisms
- `ExperimentConfiguration` - for controlling the experimentation process
- `ExperimentCycle` - for orchestrating the experimentation process`

#### Methods

`void runExperiment()`
Executes the complete experiment process:
1. Seeds the ScoredOrganismRepository by calling the Seeder (the Seeder evaluates organisms and stores them)
2. Runs the number of experiment cycles specified in ExperimentConfiguration

### ExperimentConfiguration

Configuration class that holds parameters for controlling the experimentation process.

ExperimentConfiguration uses the `@Component` and `@ConfigurationProperties` annotations to make it available for dependency injection and to bind configuration properties.

#### Properties

`int cycleCount`
The number of experiment cycles to run. Defaults to 100. Accessible through getter and setter methods.

`int repoCapacity`
The maximum number of ScoredOrganisms that can be stored in the ScoredOrganismRepository. Defaults to 50. Accessible through getter and setter methods.

### Classes and Interfaces for General Experiment Coordination

#### ScoredOrganism

A ScoredOrganism is a record of an organism and its score. Implements the Comparable<ScoredOrganism> interface to support binary searches in the ScoredOrganismRepository. Two ScoredOrganisms are compared using the score property.

##### Properties

`String id`
A synthetic key used to identify a ScoredOrganism record. A string representation of a UUID is default.

`Double score`
The score that resulted from evaluating the Organism.

`String organismId`
The id for the scored organism. Used to look up the Organism from the OrganismRepository.

`Organism organism`
The Organism instance associated with this score.

`int compareTo(ScoredOrganism other)`
This method implements Comparable<ScoredOrganism>.compareTo() by comparing this organism's score with the other.getScore().

#### ScoredOrganismRepository

Interface

##### Methods

`ScoredOrganism getById(String id)`
Returns a ScoredOrganism record, as identified.

`ScoredOrganism save(ScoredOrganism scoredOrganism)`
The scoredOrganism that is passed into the `save()` method does not have an ID. The ScoredOrganism that is returned has all of the same values, except the id has been populated.

`void delete(String id)`
Deletes a ScoredOrganism as identified by the id.

`int size()`
Returns the current number of ScoredOrganisms in the repository.

`ScoredOrganism getRandomFromTopPercent(float percent)`
Returns a random ScoredOrganism from the top percentage of the scores, as determined by percent. Since this is a predictive system, scores closer to 0.0 are better. The "top" percentage, therefore, is determined by the lowest scores.

`ScoredOrganism getRandomFromBottomPercent(float percent)`
Returns a random ScoredOrganism from the bottom percentage of the scores, as determined by percent. Since this is a predictive system, scores closer to 0.0 are better. The "bottom" percentage, therefore, is determined by the highest scores.

#### InMemoryScoredOrganismRepository

An in-memory implementation of the ScoredOrganismRepository interface.

Keeps records sorted by score.

##### Method Implementations

The in-memory implementation maintains a ranked List of ScoredOrganisms, so that we can efficiently search by score, as well as a Map of ScoredOrganisms so that they can be looked up by ID.

`ScoredOrganism getById(String id)`
Uses the Map of ScoredOrganisms for lookup.

`ScoredOrganism save(ScoredOrganism scoredOrganism)`
Adds scoredOrganism to the Map and the List. Uses Collections.binarySearch() to find the insertion point in the List of ranked ScoredOrganisms.

`void delete(String id)`
First looks up the ScoredOrganism from the Map using the id. Using the score property, looks up the ScoredOrganism from the List. Then deletes the ScoredOrganism from both the Map and the ranked List.

`int size()`
Returns the size of the Map (or List, they should be the same).

`ScoredOrganism getRandomFromTopPercent(float percent)`
First determines the subset of the List to choose from. Since the List is sorted, simply use the size of the List to determine the cutoff of the top scoring Organisms, then use a random number to choose a ScoredOrganism from the subset.

`ScoredOrganism getRandomFromBottomPercent(float percent)`
First determines the subset of the List to choose from. Since the List is sorted, simply use the size of the List to determine the cutoff of the bottom scoring Organisms, then use a random number to choose a ScoredOrganism from the subset.

## Seeding

Seed is the first step in Experimentation. A Seeder creates organisms, evaluates them, and stores the evaluated organisms in the ScoredOrganismRepository.

### Classes and Interfaces to Support Seeding

#### Seeder

Interface. Seeders provide initial evaluated and scored Organisms for the overall Experiment.

##### Methods

`void seed()`
The seed method of a concrete Seeder will create Organisms, evaluate them using its injected Evaluator, and store both the Organisms and their ScoredOrganisms in the injected repositories.

#### BasicSeeder

The BasicSeeder is a very basic implementation of the Seeder interface. It statically defines 5 Organisms, evaluates them, and loads both the Organisms and ScoredOrganisms into their respective repositories.

BasicSeeder uses the `@Component` annotation to make itself available to other classes.

##### Organisms

**Simple Arithmetic Organism**

Chromosome 1: AdditionGene → MultiplicationGene
This organism first adds 1.5 to an input value, then multiplies by 1.5
Useful for demonstrating basic sequential gene operations

**Trigonometric Analysis Organism**

Chromosome 1: SineGene → MultiplicationGene
This organism applies sine function and then amplifies the result by 1.5
Demonstrates combination of trigonometric and arithmetic operations

**Data Transformation Organism**

Chromosome 1: AdditionGene → SubtractionGene
Chromosome 2: MultiplicationGene → DivisionGene
Two parallel chromosomes: one for additive operations, another for multiplicative
Shows how multiple chromosomes can process data differently

**Reductive Processing Organism**

Chromosome 1: DivisionGene → SubtractionGene → SineGene
A 3-gene chromosome that reduces values, normalizes, and applies trigonometry
Demonstrates longer processing chains

**Basic Composite Organism**

Chromosome 1: MultiplicationGene
Chromosome 2: AdditionGene → SineGene
Chromosome 3: SubtractionGene
Simple organism with 3 chromosomes showing different gene combinations
Useful for testing organism-level data flow

## Experiment Cycle

The Experiment Cycle is the fundamental unit of experimentation. Two organisms are chosen from the ScoredOrganismRepository. They are bred to generate one or more child organisms. The children are mutated to introduce new behavior. The children are evaluated. The children are then added to the ScoredOrganismRepository based on the repository maintenance algorithm.

### Experiment Cycle Phases

#### Parent Selection

The default algorithm is to choose one parent from the top 10% of organisms, and the other parent from the bottom 90%, based on evaluation score.

#### Breeding

The default algorithm is to use the BasicOrganismBreeder to breed two parents, which will generate two children.

#### Mutation

The child organisms are mutated one or more times between 1 and 5 times randomly.

#### Child Evaluation

The child organisms are evaluated. The default algorithm is to use the BasicEvaluator to score the children.

#### ScoredOrganismRepository Maintenance

When the child organisms are scored, they are added to the ScoredOrganismRepository based on the following algorithm:

**If the ScoredOrganismRepository has not reached capacity (as defined by repoCapacity in ExperimentConfiguration):**
The child organisms are simply added to the ScoredOrganismRepository.

**If the ScoredOrganismRepository is at capacity:**
The children organisms are compared to the other members of their "family" (the two parents) and either discarded or added to the ScoredOrganismRepository by replacing parents.

The algorithm combines parents and children into a single sorted list. If the top two organisms are the parents, no changes are made (preserving the best parent). If one of the top two is a child, the worst parent is deleted from the ScoredOrganismRepository and replaced with that child. If both of the top two are children, both parents are deleted from the ScoredOrganismRepository and replaced with both children.

### Classes to Support the Experiment Cycle

The package for the experimentation classes extends from the root package of the project with /experiment

#### MutationCommand

Interface

MutationCommands are generated by classes that implement the `Mutational` interface. They represent a single mutation possibility on a single genetic element.

##### Methods

`void execute()`
Performs the mutation.

`String getDescription()`
Returns a description of the mutation.

#### Mutational

Interface

##### Methods

`List<MutationCommand> getMutationCommandList()`
Returns a list of all possible mutations for this genetic element.

#### ExperimentCycle

Interface

An ExperimentCycle class implements the phases, mostly by delegating to injected objects.

##### Methods

`void mutationCycle()`
Executes a complete mutation cycle including parent selection, breeding, mutation, evaluation, and repository maintenance.

`List<ScoredOrganism> selectParents()`
Selects parent organisms for breeding. Default algorithm chooses one parent from the top 10% and one from the bottom 90%.

`List<Organism> breedParents(List<Organism> parents)`
Breeds the selected parents to generate child organisms.

`void mutateChildren(List<Organism> children)`
Mutates the child organisms to introduce new behavior.

`List<ScoredOrganism> evaluateChildren(List<Organism> children)`
Evaluates the child organisms and returns them with their scores.

`void maintainRepository(List<ScoredOrganism> parents, List<ScoredOrganism> children)`
Maintains the ScoredOrganismRepository by potentially replacing parents with better-performing children, based on repository capacity.

#### ExperimentCycleImpl

The default implementation of the ExperimentCycle interface

##### Autowired Dependencies

The ExperimentCycleImpl depends on Autowired instances of:
- `OrganismRepository`
- `ScoredOrganismRepository`
- `OrganismBreeder`
- `Evaluator`
- `ExperimentConfiguration`

##### Method Implementations

**mutationPhase()**

Executes a complete mutation cycle by calling each phase method in sequence.

**selectParents()**

Chooses one parent from the top 10% of the ScoredOrganismRepository, and one parent from the bottom 90%.

**breedParents(List<Organism> parents)**

Uses the injected OrganismBreeder to generate a list of child Organisms.

**mutateChildren(List<Organism> children)**

Mutates each of the children a random number (between 1 and 5) of times.

**evaluateChildren(List<Organism> children)**

Uses the injected Evaluator to evaluate the child organisms and returns a list of ScoredOrganisms.

**maintainRepository(List<ScoredOrganism> parents, List<ScoredOrganism> children)**

Checks if the ScoredOrganismRepository is at capacity (using ExperimentConfiguration.repoCapacity). If not at capacity, simply adds the children. If at capacity, compares parents and children and replaces parents with better-performing children according to the algorithm described in the ScoredOrganismRepository Maintenance section above.

## Evaluation

### BasicEvaluator

A concrete implementation of the Evaluator interface that provides fitness scoring for organisms using prediction-based evaluation methodology. It evaluates organisms by feeding them historical time-series data and measuring their prediction accuracy.

BasicEvaluator uses the `@Component` annotation to make itself available for dependency injection.

#### Methods and Properties

`String trainingDataPath`
The path to the CSV file containing historical training data. Defaults to "/training-data/HistoricalPrices-reversed.csv". Accessible through getter and setter methods.

`int targetIndex`
Specifies which data column (by index) contains the target values to predict. Defaults to 1. Accessible through getter and setter methods.

`int leadConsumptionCount`
Defines the number of data points the organism processes before making predictions. Defaults to 3. Accessible through getter and setter methods with validation to ensure the value is at least 1.

`List<DataQuantum> historicalData`
Cached historical data loaded from the CSV file. Uses lazy loading - data is loaded only when first needed during evaluation.

`BasicEvaluator()`
Default constructor using sensible defaults. Sets targetIndex to 1 (typically the "Open" column in stock data) and leadConsumptionCount to 3.

`BasicEvaluator(int targetIndex, int leadConsumptionCount)`
Constructor with custom configuration parameters.

`double evaluate(Organism organism)`
Implements the Evaluator interface. Evaluates an organism by feeding it historical data and measuring prediction accuracy. Returns the cumulative prediction error score where lower values indicate better performance (0 represents perfect accuracy).

**Evaluation Process:**
- Loads historical data from CSV file if not already cached
- Uses an internal EvaluationState to manage prediction timing through a queue-based buffering system
- Feeds each DataQuantum to the organism in sequence
- Captures organism predictions (final DataPoint value from each consumption)
- Maintains a lead-in period defined by leadConsumptionCount before comparing predictions to actual values
- Calculates absolute difference between predicted and actual target values
- Returns accumulated error as the fitness score

`void setHistoricalData(List<DataQuantum> historicalData)`
Sets the historical data used for evaluation. Useful for testing scenarios.

#### Private Helper Methods

`List<DataQuantum> loadHistoricalData()`
Loads and parses historical data from the CSV file using Java streams. Converts each data row into a DataQuantum with epoch timestamp and numerical values.

`DataQuantum parseDataQuantumFromLine(String line)`
Parses a single CSV line into a DataQuantum. The first column is expected to contain date information, with subsequent columns containing numerical data values.

`long parseDateToEpoch(String dateStr)`
Parses date strings in MM/dd/yy format to epoch milliseconds. Assumes years starting with "20" and converts dates to midnight UTC.

#### Inner Classes

`EvaluationState`
Private inner class that manages the prediction timing mechanism using a queue-based approach. Tracks whether the lead consumption count has been met and provides buffered access to predictions for comparison against actual values.

#### Example Scenario

Consider evaluating an organism with historical Dow Jones data containing columns: Date, Open, High, Low, Close. With a targetIndex of 1 (Open) and leadConsumptionCount of 3:

1. Feed the organism three DataQuanta (days 1-3), saving each output value
2. Feed the fourth DataQuantum and compare the organism's output against the actual Open value from day 4
3. Calculate the absolute difference between predicted and actual values
4. Continue this process through all remaining test data
5. Return the accumulated prediction error as the organism's fitness score

#### Data Format

The evaluation uses CSV data where the first column contains epoch dates (assuming years starting with "20"), and subsequent columns contain numerical values for analysis. Each row represents a single time point in the historical dataset.

## Server Details

### ExperimentController

The ExperimentController is in the `controller` package that inherits from the base project package. It implements the endpoints described in this document.

The ExperimentController autowires a ScoredOrganismRepository and an Experiment.

### Endpoints

#### /experiment

`/experiment` is the base context path for endpoints that manage the Experiment.

#### POST /experiment/run

Runs a complete experiment by calling the Experiment.runExperiment() method. Returns a summary of the experiment results.

#### GET /experiment/status

Returns the current status of the experiment including the number of cycles completed and the current best organism score.

## Support Features

Some features are needed to support experimentation.

### GeneGenerator

The GeneGenerator is used to provide random Genes to support mutation activities.

#### Methods

`public static Gene getRandomGene()`
Returns a single, randomly chosen Gene. If the Gene uses any operational constants, they are randomly generated. If the Gene has one targetIndex, it uses the default value of -1. If the Gene has more than one targetIndex, the others will be given random numbers between -2 and -10, with none of them repeating.

### ChromosomeGenerator

The ChromosomeGenerator is used to provide random Chromosomes to support mutation activities.

#### Methods

`public static Chromosome getRandomChromosome()`
Returns a randomly generated Chromosome created with between 3 and 6 random Genes generated by GeneGenerator.