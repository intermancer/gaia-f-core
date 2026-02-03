# Experimentation

This document describes the Classes and changes to existing Classes to enable experimentation.

## Experiment Coordination

An Experiment is composed of seeding a repository with Scored Organisms, and then running a number of Experiment Cycles.

### Experiment

Interface. An Experiment is responsible for orchestrating the complete experimentation process.

#### Methods

`void runExperiment()`
Executes the complete experiment process, including creating and managing its own ExperimentStatus.

`String getId()`
Returns the unique identifier for this experiment.

`void pause()`
Pauses the experiment execution. The experiment will stop processing cycles but maintain its current state for later resumption. Only valid when the experiment is in RUNNING state.

`void resume()`
Resumes a paused experiment. The experiment will continue processing cycles from where it left off. Only valid when the experiment is in PAUSED state.

`boolean isPaused()`
Returns true if the experiment is currently paused, false otherwise.

### BasicExperimentImpl

Implementation of the Experiment interface. BasicExperimentImpl uses the `@Component` annotation with prototype scope; instances are created by the ExperimentService using the ApplicationContext to resolve autowired dependencies.

#### Properties

`String experimentId`
The unique identifier for this experiment instance, generated as a UUID string when the experiment is instantiated.

`ExperimentStatus experimentStatus`
The ExperimentStatus instance associated with this experiment, created internally by the experiment when it starts running.

#### Properties

`volatile boolean paused`
A thread-safe flag indicating whether the experiment is currently paused. Uses volatile keyword to ensure visibility across threads since the experiment loop runs on one thread while pause/resume commands come from another thread.

`boolean pausable`
Indicates whether this experiment can be paused. Copied from ExperimentConfiguration when the experiment starts.

`int pauseCycles`
The number of cycles after which the experiment will automatically pause (if pausable is true). Copied from ExperimentConfiguration when the experiment starts. A value of 0 means no automatic pausing.

#### Autowired Dependencies

The BasicExperimentImpl depends on:
- `Seeder` - for initializing the repositories with evaluated seed organisms
- `ExperimentConfiguration` - for controlling the experimentation process
- `ExperimentCycle` - for orchestrating the experimentation process
- `ExperimentStatusRepository` - for persisting experiment status

#### Methods

`String getId()`
Returns the unique identifier for this experiment.

`void runExperiment()`
Executes the complete experiment process:
1. Copies pausable and pauseCycles values from ExperimentConfiguration to local properties
2. Creates a new ExperimentStatus instance and associates it with this experiment
3. Sets the status to RUNNING and persists it to the ExperimentStatusRepository
4. Logs the experiment start with experiment ID and cycle count
5. Seeds the ScoredOrganismRepository by calling the Seeder (the Seeder evaluates organisms and stores them)
6. Runs the number of experiment cycles specified in ExperimentConfiguration
7. During each cycle iteration, checks the paused flag and blocks if paused (using wait/sleep mechanism)
8. If pausable is true and pauseCycles > 0, automatically pauses when cyclesCompleted reaches pauseCycles
9. Logs a dot (`.`) every 100 cycles for progress tracking
10. Increments cyclesCompleted in ExperimentStatus after each cycle
11. Sets status to STOPPED upon successful completion and logs completion
12. Sets status to EXCEPTION and logs error if an exception occurs during execution

`void pause()`
Sets the paused flag to true and updates the ExperimentStatus to PAUSED state. Logs the pause action with the experiment ID.

`void resume()`
Sets the paused flag to false and updates the ExperimentStatus to RUNNING state. Logs the resume action with the experiment ID. Notifies any waiting threads to continue execution.

`boolean isPaused()`
Returns the current value of the paused flag.

### ExperimentConfiguration

Configuration class that holds parameters for controlling the experimentation process.

ExperimentConfiguration uses the `@Component` and `@ConfigurationProperties` annotations to make it available for dependency injection and to bind configuration properties.

#### Properties

`int cycleCount`
The number of experiment cycles to run. Defaults to 1000. Accessible through getter and setter methods.

`int repoCapacity`
The maximum number of ScoredOrganisms that can be stored in the ScoredOrganismRepository. Defaults to 50. Accessible through getter and setter methods.

`boolean pausable`
Indicates whether experiments created with this configuration can be paused and resumed. Defaults to false. Accessible through getter and setter methods.

`int pauseCycles`
The number of cycles after which an experiment will automatically pause (if pausable is true). A value of 0 means the experiment will not automatically pause and can only be paused manually through the pause endpoint. Defaults to 250. Accessible through getter and setter methods.

### ExperimentState

An enum that defines the possible operational states of an experiment.

#### Values

`STOPPED`
The experiment is not currently running. This is the initial state before an experiment starts and the final state after successful completion.

`RUNNING`
The experiment is actively executing cycles.

`PAUSED`
The experiment has been paused and is not executing cycles, but maintains its current state and can be resumed to continue from where it left off.

`EXCEPTION`
The experiment encountered an error and has stopped abnormally.

### ExperimentStatus

A data class that tracks the runtime state and progress of an experiment. ExperimentStatus maintains information about the experiment's current execution state, performance metrics, and operational statistics.

ExperimentStatus instances are created and managed by the Experiment implementations. When an experiment starts running, it creates a new ExperimentStatus instance and persists it to the ExperimentStatusRepository. This allows multiple experiments to run concurrently, each with its own status tracking.

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
- `PAUSED` - The experiment is paused and can be resumed
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

`void mutationCycle(String experimentId, ExperimentStatus experimentStatus)`
Executes a complete mutation cycle including parent selection, breeding, mutation, evaluation, and repository maintenance. Takes the experimentId to track organisms and the experimentStatus to update progress metrics.

`List<ScoredOrganism> selectParents(String experimentId)`
Selects parent organisms for breeding from the specified experiment. Default algorithm chooses one parent from the top 10% and one from the bottom 90%.

`List<Organism> breedParents(List<Organism> parents)`
Breeds the selected parents to generate child organisms.

`void mutateChildren(List<Organism> children)`
Mutates the child organisms to introduce new behavior.

`List<ScoredOrganism> evaluateChildren(List<Organism> children, String experimentId)`
Evaluates the child organisms and returns them with their scores. Each ScoredOrganism is tagged with the experimentId.

`void maintainRepository(List<ScoredOrganism> parents, List<ScoredOrganism> children, String experimentId, ExperimentStatus experimentStatus)`
Maintains the ScoredOrganismRepository by potentially replacing parents with better-performing children, based on repository capacity. Updates the experimentStatus with replacement counts.

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

**mutationCycle(String experimentId, ExperimentStatus experimentStatus)**

Executes a complete mutation cycle by calling each phase method in sequence, passing the experimentId and experimentStatus through the pipeline.

**selectParents(String experimentId)**

Chooses one parent from the top 10% of the ScoredOrganismRepository for the specified experiment, and one parent from the bottom 90%.

**breedParents(List<Organism> parents)**

Uses the injected OrganismBreeder to generate a list of child Organisms.

**mutateChildren(List<Organism> children)**

Mutates each of the children a random number (between 1 and 5) of times.

**evaluateChildren(List<Organism> children, String experimentId)**

Uses the injected Evaluator to evaluate the child organisms and returns a list of ScoredOrganisms. Each ScoredOrganism is created with the experimentId parameter.

**maintainRepository(List<ScoredOrganism> parents, List<ScoredOrganism> children, String experimentId, ExperimentStatus experimentStatus)**

Checks if the ScoredOrganismRepository is at capacity for the given experiment (using ExperimentConfiguration.repoCapacity). If not at capacity, simply adds the children. If at capacity, compares parents and children and replaces parents with better-performing children according to the algorithm described in the ScoredOrganismRepository Maintenance section above. Updates the experimentStatus.organismsReplaced counter when replacements occur.

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

### ExperimentService

The ExperimentService is in the `service` package that inherits from the base project package. It provides the business logic for managing experiments, delegating to repositories and coordinating the experiment lifecycle.

The ExperimentService is a Spring `@Service` component that autowires:
- `ApplicationContext` - for instantiating Experiment beans with resolved dependencies
- `ExperimentRepository` - for persisting and retrieving experiments
- `ExperimentStatusRepository` - for persisting and retrieving experiment status
- `ExperimentConfiguration` - the singleton configuration component
- `ObjectProvider<ExperimentService>` - for enabling asynchronous execution through Spring proxy

#### Key Design Patterns

**Asynchronous Execution**: The ExperimentService uses `@Async` on the `runExperimentAsync()` method to run experiments on separate threads. To enable this, it uses `ObjectProvider<ExperimentService>` to obtain a reference to its Spring proxy, avoiding circular dependencies while allowing proper AOP interception.

**Configuration Management**: The service manages two types of configuration:
1. Component configuration - the current ExperimentConfiguration singleton that will be used for the next experiment
2. Experiment-specific configuration - the configuration snapshot that was used when a specific experiment was created (currently returns the singleton, but designed to support per-experiment configuration in the future)

#### Public Methods

`String startExperiment()`
Starts a new experiment by:
1. Instantiating a new Experiment using ApplicationContext (which generates its own ID)
2. Saving the experiment to ExperimentRepository
3. Logging the experiment start with its ID
4. Calling `runExperimentAsync()` through the service proxy to execute on a separate thread
5. Returning the experiment ID

Returns the experiment ID as a String.

`void runExperimentAsync(Experiment experiment)`
Marked with `@Async` to run on a separate thread. Calls `experiment.runExperiment()` to execute the complete experiment process. This method must be called through the Spring proxy (via ObjectProvider) to enable asynchronous behavior.

`ExperimentConfiguration getComponentConfiguration()`
Returns the current ExperimentConfiguration singleton - the configuration that will be used for the next experiment started.

`ExperimentConfiguration updateComponentConfiguration(ExperimentConfiguration updatedConfig)`
Updates the component configuration with new values for cycleCount, repoCapacity, pausable, and pauseCycles. Returns the updated configuration.

`ExperimentConfiguration getExperimentConfiguration(String experimentId)`
Retrieves the configuration for a specific experiment by experimentId. Currently returns the singleton configuration component. In the future, this could be enhanced to return experiment-specific configuration snapshots. Throws `IllegalArgumentException` if the experiment is not found.

`ExperimentStatus getStatus(String experimentId)`
Retrieves the ExperimentStatus for a specific experiment by looking it up using the experimentId in the ExperimentStatusRepository. Throws `IllegalArgumentException` if the status is not found.

`void pauseExperiment(String experimentId)`
Pauses a running experiment by:
1. Looking up the Experiment instance from the ExperimentRepository using the experimentId
2. Validating that the experiment is in RUNNING state
3. Calling the experiment's `pause()` method
4. Logging the pause action

Throws `IllegalArgumentException` if the experiment is not found or is not in a pausable state (must be RUNNING).

`void resumeExperiment(String experimentId)`
Resumes a paused experiment by:
1. Looking up the Experiment instance from the ExperimentRepository using the experimentId
2. Validating that the experiment is in PAUSED state
3. Calling the experiment's `resume()` method
4. Logging the resume action

Throws `IllegalArgumentException` if the experiment is not found or is not in PAUSED state.

### ExperimentController

The ExperimentController is in the `controller` package that inherits from the base project package. It implements the REST endpoints described in this document and delegates all business logic to ExperimentService.

The ExperimentController is a Spring `@RestController` that autowires only ExperimentService. It acts as a thin HTTP layer, mapping REST requests to service method calls.

#### Design Philosophy

The controller follows the principle of separation of concerns:
- **Controller Layer**: Handles HTTP request/response mapping, path variables, request bodies, and response entities
- **Service Layer**: Contains all business logic, repository interactions, and transaction management

#### Properties

None. The controller maintains no state and delegates all operations to ExperimentService.

#### Public Methods

`ResponseEntity<String> startExperiment()`
Starts a new experiment by delegating to `experimentService.startExperiment()`. Returns the experiment ID as plain text. Mapped to POST `/experiment/start`.

`ResponseEntity<ExperimentConfiguration> getComponentConfiguration()`
Returns the current component configuration by delegating to `experimentService.getComponentConfiguration()`. Mapped to GET `/experiment/configuration`.

`ResponseEntity<ExperimentConfiguration> updateComponentConfiguration(ExperimentConfiguration updatedConfig)`
Updates the component configuration by delegating to `experimentService.updateComponentConfiguration(updatedConfig)`. Returns the updated configuration. Mapped to PUT `/experiment/configuration`.

`ResponseEntity<ExperimentConfiguration> getExperimentConfiguration(String experimentId)`
Retrieves the configuration for a specific experiment by delegating to `experimentService.getExperimentConfiguration(experimentId)`. Mapped to GET `/experiment/{experimentId}/configuration`.

`ResponseEntity<ExperimentStatus> getStatus(String experimentId)`
Retrieves the ExperimentStatus for a specific experiment by delegating to `experimentService.getStatus(experimentId)`. Logs "Pinging status..." for monitoring purposes. Mapped to GET `/experiment/{experimentId}/status`.

`ResponseEntity<Void> pauseExperiment(String experimentId)`
Pauses a running experiment by delegating to `experimentService.pauseExperiment(experimentId)`. Returns HTTP 200 OK on success, or HTTP 400 Bad Request if the experiment is not in a pausable state. Mapped to POST `/experiment/{experimentId}/pause`.

`ResponseEntity<Void> resumeExperiment(String experimentId)`
Resumes a paused experiment by delegating to `experimentService.resumeExperiment(experimentId)`. Returns HTTP 200 OK on success, or HTTP 400 Bad Request if the experiment is not in PAUSED state. Mapped to POST `/experiment/{experimentId}/resume`.

### Endpoints

#### /experiment

`/experiment` is the base context path for endpoints that manage the Experiment.

#### POST /experiment/start

Starts a new experiment by instantiating an Experiment, saving it to the repository, and executing it asynchronously on a separate thread.

Returns a plain text response containing the experiment ID:
```
550e8400-e29b-41d4-a716-446655440000
```

The experiment runs asynchronously, so the response is returned immediately while the experiment executes in the background. Clients should poll the status endpoint to monitor progress.

#### GET /experiment/configuration

Returns the current component configuration - the ExperimentConfiguration that will be used for the next experiment started.

Returns a JSON response containing:
- `cycleCount` - The number of experiment cycles to run
- `repoCapacity` - The maximum number of ScoredOrganisms that can be stored

Example response:
```json
{
  "cycleCount": 1500,
  "repoCapacity": 200,
  "pausable": false,
  "pauseCycles": 250
}
```

#### PUT /experiment/configuration

Updates the component configuration with new values. This affects the configuration that will be used for the next experiment started.

Accepts a JSON body with:
- `cycleCount` - The new number of experiment cycles to run
- `repoCapacity` - The new maximum repository capacity
- `pausable` - Whether experiments can be paused
- `pauseCycles` - Number of cycles after which to auto-pause (0 for no auto-pause)

Returns the updated ExperimentConfiguration object.

Example request body:
```json
{
  "cycleCount": 2000,
  "repoCapacity": 250,
  "pausable": true,
  "pauseCycles": 500
}
```

#### GET /experiment/{experimentId}/configuration

Retrieves the configuration that was used when a specific experiment was created.

Path parameter:
- `experimentId` - The unique identifier of the experiment

Returns the ExperimentConfiguration object. Currently returns the singleton component configuration, but designed to support per-experiment configuration snapshots in the future.

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

Example response:
```json
{
  "id": "450e8400-e29b-41d4-a716-446655440001",
  "experimentId": "550e8400-e29b-41d4-a716-446655440000",
  "cyclesCompleted": 750,
  "organismsReplaced": 42,
  "status": "RUNNING"
}
```

This endpoint is typically polled by clients (e.g., every 1 second) to monitor experiment progress in real-time.

#### POST /experiment/{experimentId}/pause

Pauses a running experiment, allowing it to be resumed later from the same point.

Path parameter:
- `experimentId` - The unique identifier of the experiment to pause

The experiment must be in RUNNING state for this operation to succeed. When paused:
- The experiment stops executing cycles but maintains its current state
- The ExperimentStatus is updated to PAUSED
- All progress metrics (cyclesCompleted, organismsReplaced) are preserved
- The experiment thread blocks until resumed

Returns HTTP 200 OK on success, or HTTP 400 Bad Request if the experiment is not in a pausable state.

This endpoint is typically called by users through the UI when they want to temporarily halt an experiment without losing progress.

#### POST /experiment/{experimentId}/resume

Resumes a paused experiment, continuing execution from where it left off.

Path parameter:
- `experimentId` - The unique identifier of the experiment to resume

The experiment must be in PAUSED state for this operation to succeed. When resumed:
- The experiment continues executing cycles from its current position
- The ExperimentStatus is updated to RUNNING
- All progress metrics continue from their paused values
- The experiment thread is notified to continue execution

Returns HTTP 200 OK on success, or HTTP 400 Bad Request if the experiment is not in PAUSED state.

This endpoint is typically called by users through the UI when they want to continue a previously paused experiment.

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