# Experimentation

This document describes the Classes and changes to existing Classes to enable experimentation.

## Experiment Coordination

An Experiment is composed of seeding a repository with Scored Organisms, and then running a number of Experiment Cycles.

### Experiment

Interface.  An Experiment is responsible for orchestrating the complete experimentation process.

#### Properties

`ExperimentStatus experimentStatus`
The ExperimentStatus instance associated with this experiment. This creates a bi-directional relationship where the Experiment holds a reference to its ExperimentStatus, and the ExperimentStatus holds the experiment's ID.

#### Methods

`void runExperiment()`
Executes the complete experiment process.

`String getId()`
Returns the unique identifier for this experiment.

`ExperimentStatus getExperimentStatus()`
Returns the ExperimentStatus instance associated with this experiment.

`void setExperimentStatus(ExperimentStatus experimentStatus)`
Sets the ExperimentStatus instance for this experiment and establishes the bi-directional relationship by setting the experimentId on the status object.

### BasicExperimentImpl

Implementation of the Experiment interface. BasicExperimentImpl does not use the `@Component` annotation; instead, instances are created and managed by the ExperimentController using the ApplicationContext to resolve autowired dependencies.

#### Properties

`String experimentId`
The unique identifier for this experiment instance, generated as a UUID string when the experiment is instantiated.

`ExperimentStatus experimentStatus`
The ExperimentStatus instance associated with this experiment. This property establishes the bi-directional relationship with ExperimentStatus.

#### Autowired Dependencies

The BasicExperimentImpl depends on:
- `Seeder` - for initializing the repositories with evaluated seed organisms
- `ExperimentConfiguration` - for controlling the experimentation process
- `ExperimentCycle` - for orchestrating the experimentation process

#### Methods

`String getId()`
Returns the unique identifier for this experiment.

`ExperimentStatus getExperimentStatus()`
Returns the ExperimentStatus instance associated with this experiment.

`void setExperimentStatus(ExperimentStatus experimentStatus)`
Sets the ExperimentStatus instance for this experiment. This method also sets the experimentId on the ExperimentStatus object to establish the bi-directional relationship.

`void runExperiment()`
Executes the complete experiment process:
1. Resets and updates the associated ExperimentStatus
2. Seeds the ScoredOrganismRepository by calling the Seeder (the Seeder evaluates organisms and stores them)
3. Runs the number of experiment cycles specified in ExperimentConfiguration
4. Updates the ExperimentStatus after each cycle and upon completion

### ExperimentConfiguration

Configuration class that holds parameters for controlling the experimentation process.

ExperimentConfiguration uses the `@Component` and `@ConfigurationProperties` annotations to make it available for dependency injection and to bind configuration properties.

#### Properties

`int cycleCount`
The number of experiment cycles to run. Defaults to 1000. Accessible through getter and setter methods.

`int repoCapacity`
The maximum number of ScoredOrganisms that can be stored in the ScoredOrganismRepository. Defaults to 50. Accessible through getter and setter methods.

### ExperimentStatus

A data class that tracks the runtime state and progress of an experiment. ExperimentStatus maintains information about the experiment's current execution state, performance metrics, and operational statistics.

ExperimentStatus instances are created and managed by the ExperimentController. When an experiment starts, the controller creates a new ExperimentStatus instance, associates it bi-directionally with the Experiment, and persists both to their respective repositories. This allows multiple experiments to run concurrently, each with its own status tracking.

#### Properties

`String id`
A synthetic key used to identify an ExperimentStatus record. A string representation of a UUID, automatically generated when the instance is created.

`String experimentId`
The id of the experiment associated with this status. Links the status to its corresponding Experiment, establishing the bi-directional relationship.

`int cyclesCompleted`
The number of experiment cycles that have been completed since the experiment started. Defaults to 0. Accessible through getter and setter methods.

`int organismsReplaced`
The count of organisms that have been replaced in the ScoredOrganismRepository after it reached capacity. This metric tracks evolutionary progress by counting successful replacements during repository maintenance. Defaults to 0. Accessible through getter and setter methods.

`ExperimentState status`
The current operational state of the experiment. Possible values are:
- `STOPPED` - The experiment is not currently running
- `RUNNING` - The experiment is actively executing cycles
- `EXCEPTION` - The experiment encountered an error and has stopped

Accessible through getter and setter methods.

#### Methods

`ExperimentStatus()`
Default constructor that generates a unique ID for this ExperimentStatus instance.

`String getId()`
Returns the unique identifier for this status record.

`void setId(String id)`
Sets the unique identifier for this status record.

`String getExperimentId()`
Returns the ID of the associated experiment.

`void setExperimentId(String experimentId)`
Sets the ID of the associated experiment, establishing the link to the Experiment.

`void reset()`
Resets all tracking metrics to their initial state. Sets cyclesCompleted to 0, organismsReplaced to 0, and status to STOPPED.

`void incrementCyclesCompleted()`
Increments the cyclesCompleted counter by 1. Called after each successful experiment cycle.

`void incrementOrganismsReplaced()`
Increments the organismsReplaced counter by 1. Called when an organism is successfully replaced in the ScoredOrganismRepository during repository maintenance.

`void incrementOrganismsReplaced(int count)`
Increments the organismsReplaced counter by the specified count. Used when multiple organisms are replaced in a single maintenance operation.

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

`String experimentId`
The id of the experiment that produced this score.

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

`int size(String experimentId)`
Returns the current number of ScoredOrganisms in the repository for the given experimentId.

`List<String> getAllOrganismIds(String experimentId)`
Returns a list of all ScoredOrganism IDs currently stored in the repository with the given experimentId. The list contains the id property from each ScoredOrganism, not the organismId property.

`ScoredOrganism getRandomFromTopPercent(String experimentId, float percent)`
Returns a random ScoredOrganism from the top percentage of the scores, as determined by percent, with the given experimentId. Since this is a predictive system, scores closer to 0.0 are better. The "top" percentage, therefore, is determined by the lowest scores.

`ScoredOrganism getRandomFromBottomPercent(String experimentId, float percent)`
Returns a random ScoredOrganism from the bottom percentage of the scores, as determined by percent, with the given experimentId. Since this is a predictive system, scores closer to 0.0 are better. The "bottom" percentage, therefore, is determined by the highest scores.

#### InMemoryScoredOrganismRepository

An in-memory implementation of the ScoredOrganismRepository interface.

Keeps records sorted by score.

##### Method Implementations

For each experimentId, the in-memory implementation maintains a ranked List of ScoredOrganisms, so that we can efficiently search by score, as well as a Map of ScoredOrganisms, which we will refer to as the "identity Map", so that they can be looked up by ID. It uses a Map of Lists to maintain the order, which we will refer to as the "ordered Map".

`ScoredOrganism getById(String id)`
Uses the identity Map of ScoredOrganisms for lookup.

`ScoredOrganism save(ScoredOrganism scoredOrganism)`
First checks to see if the ordered Map already contains a List for the given experimentId. If not, creates a new List and adds it to the Map. Adds scoredOrganism to the identity Map and the experiment-ordered List. Uses Collections.binarySearch() to find the insertion point in the List of ranked ScoredOrganisms.

`void delete(String id)`
First looks up the ScoredOrganism from the identity Map using the id. Uses the experimentId of the ScoredOrganism to find the ordered List, and uses the score property to look up the ScoredOrganism from the List. Then deletes the ScoredOrganism from both the identity Map and the ranked List.

`int size(String experimentId)`
Returns the size of the ordered List for the given experimentId.

`List<String> getAllOrganismIds(String experimentId)`
Returns a list of all ScoredOrganism IDs by extracting the id property from each ScoredOrganism in the List of ranked ScoredOrganisms for the given experimentId.

`ScoredOrganism getRandomFromTopPercent(String experimentId, float percent)`
First retrieves the appropriate List from the ordered Map, then determines the subset of the List to choose from. Since the List is sorted, simply use the size of the List to determine the cutoff of the top scoring Organisms, then use a random number to choose a ScoredOrganism from the subset.

`ScoredOrganism getRandomFromBottomPercent(String experimentId, float percent)`
First retrieves the appropriate List from the ordered Map, then determines the subset of the List to choose from. Since the List is sorted, simply use the size of the List to determine the cutoff of the bottom scoring Organisms, then use a random number to choose a ScoredOrganism from the subset.

## Seeding

Seed is the first step in Experimentation. A Seeder creates organisms, evaluates them, and stores the evaluated organisms in the ScoredOrganismRepository.

### Classes and Interfaces to Support Seeding

#### Seeder

Interface. Seeders provide initial evaluated and scored Organisms for the overall Experiment.

##### Methods

`void seed(String experimentId)`
The seed method of a concrete Seeder will create Organisms, evaluate them using its injected Evaluator, and store both the Organisms and their ScoredOrganisms in the injected repositories.  ScoredOrganisms require an experimentId to be associated with them, so that they can be stored in the correct repository.

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

Uses the injected Evaluator to evaluate the child organisms and returns a list of ScoredOrganisms.  Each ScoredOrganism is created with the current experiment's experimentId.

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

The ExperimentController autowires an ApplicationContext, ScoredOrganismRepository, ExperimentConfiguration, ExperimentStatusRepository, and ExperimentRepository.

The ExperimentController is responsible for managing Experiment instances. It uses the ApplicationContext to instantiate Experiments and resolve their autowired dependencies.

The ExperimentController manages the bi-directional relationship between Experiment and ExperimentStatus. When an experiment is started:
1. Creates a new ExperimentStatus instance (which generates its own ID)
2. Instantiates a new Experiment using the ApplicationContext (which generates its own ID)
3. Associates the ExperimentStatus with the Experiment by calling `experiment.setExperimentStatus(status)`, which sets the experimentId on the status
4. Persists the ExperimentStatus to the ExperimentStatusRepository
5. Persists the Experiment to the ExperimentRepository
6. Starts the experiment by calling `experiment.runExperiment()`

This ensures both entities are properly persisted and maintain their bi-directional relationship throughout the experiment lifecycle.

#### Properties

None. The controller does not maintain in-memory lists; instead, it uses repositories to persist and retrieve experiments and their statuses.

#### Public Methods

`ResponseEntity<ExperimentResponse> startExperiment()`
Starts a new experiment by creating a new ExperimentStatus instance, instantiating a new Experiment using the ApplicationContext, establishing the bi-directional relationship between them, persisting both to their respective repositories, and calling the Experiment.runExperiment() method. Returns a response containing the experiment ID. Mapped to POST `/experiment/start`.

`ResponseEntity<ExperimentConfiguration> getConfiguration()`
Returns the current ExperimentConfiguration. Mapped to GET `/experiment/configuration`.

`ResponseEntity<ExperimentConfiguration> updateConfiguration(ExperimentConfiguration updatedConfig)`
Updates the experiment configuration with new values for cycleCount and repoCapacity. Returns the updated configuration. Mapped to PUT `/experiment/configuration`.

`ResponseEntity<Experiment> getExperiment(String experimentId)`
Retrieves an Experiment by its ID from the ExperimentRepository. Returns a 404 if the experiment is not found. Mapped to GET `/experiment/{experimentId}`.

`ResponseEntity<ExperimentStatus> getStatus(String experimentId)`
Retrieves the ExperimentStatus for a specific experiment by looking it up using the experimentId in the ExperimentStatusRepository. Returns a 404 if the status is not found. Mapped to GET `/experiment/{experimentId}/status`.

`ResponseEntity<List<Experiment>> getAllExperiments()`
Retrieves all experiments from the ExperimentRepository. Mapped to GET `/experiment/all`.

### Endpoints

#### /experiment

`/experiment` is the base context path for endpoints that manage the Experiment.

#### POST /experiment/start

Starts a new experiment by creating and persisting both an Experiment and its associated ExperimentStatus, establishing their bi-directional relationship, and calling the Experiment.runExperiment() method.

Returns a JSON response containing:
- `experimentId` - The unique identifier of the newly created experiment
- `message` - A confirmation message

Example response:
```json
{
  "experimentId": "550e8400-e29b-41d4-a716-446655440000",
  "message": "Experiment started successfully"
}
```

#### GET /experiment/configuration

Returns the current ExperimentConfiguration object containing:
- `cycleCount` - The number of experiment cycles to run
- `repoCapacity` - The maximum number of ScoredOrganisms that can be stored

#### PUT /experiment/configuration

Updates the experiment configuration with new values. Accepts a JSON body with:
- `cycleCount` - The new number of experiment cycles to run
- `repoCapacity` - The new maximum repository capacity

Returns the updated ExperimentConfiguration object.

#### GET /experiment/{experimentId}

Retrieves a specific Experiment by its ID.

Path parameter:
- `experimentId` - The unique identifier of the experiment

Returns the Experiment object if found, or a 404 status if not found.

#### GET /experiment/{experimentId}/status

Retrieves the ExperimentStatus for a specific experiment.

Path parameter:
- `experimentId` - The unique identifier of the experiment whose status should be retrieved

Returns the ExperimentStatus object containing:
- `id` - The unique identifier of the status record
- `experimentId` - The ID of the associated experiment
- `cyclesCompleted` - The number of experiment cycles completed
- `organismsReplaced` - The count of organisms replaced during repository maintenance
- `status` - The current experiment state (STOPPED, RUNNING, or EXCEPTION)

Returns a 404 status if the experiment status is not found.

#### GET /experiment/all

Retrieves all experiments from the ExperimentRepository.

Returns a list of all Experiment objects.

## Support Features

Some features are needed to support experimentation.

### GeneGenerator

The GeneGenerator is used to provide random Genes to support mutation activities.

#### Methods

`public static Gene getRandomGene()`
Returns a single, randomly chosen Gene. If the Gene uses any operational constants, they are randomly generated. If the Gene uses one targetIndex, it uses the default value of -1. If the Gene has more than one targetIndex, the others will be given random numbers between -2 and -10, with none of them repeating.

### ChromosomeGenerator

The ChromosomeGenerator is used to provide random Chromosomes to support mutation activities.

#### Methods

`public static Chromosome getRandomChromosome()`
Returns a randomly generated Chromosome created with between 3 and 6 random Genes generated by GeneGenerator.