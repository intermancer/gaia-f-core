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

## BasicEvaluator

A concrete implementation of the Evaluator interface that provides fitness scoring for organisms using prediction-based evaluation methodology. It evaluates organisms by feeding them historical time-series data and measuring their prediction accuracy.

BasicEvaluator uses the `@Component` annotation to make itself available for dependency injection.

### Methods and properties

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
Continue this process through all remaining test data
4. Return the accumulated prediction error as the organism's fitness score

#### Data Format
The evaluation uses CSV data where the first column contains epoch dates (assuming years starting with "20"), and subsequent columns contain numerical values for analysis. Each row represents a single time point in the historical dataset.

### MutationCommand

Interface

MutationCommands are generated by classes that implement the `Mutational` interface.  They represent a single mutation possibility on a single genetic element.

#### Methods

`void execute()`
Performs the mutation.

### Mutational

Interface

#### Methods

`public List<MutationCommand> getMutationCommandList()`

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

## Support features

Some features are needed to support experimentation.

### GeneGenerator

The GeneGenerator is used to provide random Genes to support mutation activities.

#### Methods

`public static Gene getRandomGene()`
Returns a single, randomly chosen Gene. If the Gene uses any operational constants, they are randomly generated. If the Gene has one targetIndex, it uses the default value of -1.  If the Gene has more than one targetIndex, the others will be given random numbers between -2 and -10, with none of them repeating.

### ChromosomeGenerator

The ChromosomeGenerator is used to provide random Chromosomes to support mutation activities

#### Methods

`public static Chromosome getRandomChromosome()`
Returns a randomly generated Chromosome created with between 3 and 6 random Genes generated by GeneGenerator.