# GAIA-F Design

GAIA-F is a genetic algorithm framework.  It works on time-series data to look for algorithmic patterns in the data.

The time-series data is modeled as a series of DataQuanta.  Each DataQuantum contains a set of DataPoints that represent the data stream at a point in time.

Algorithms are modeled as Organisms.  Organisms have Chromosomes.  Chromosomes have Genes.  Genes represent a single operation on the DataQuanta.

## Evaluation

An Organism is evaluated by feeding it a series DataQuanta.  Each DataQuatum is fed to the Organism.  The Organism passes each DataQuantum to each of its Chromosomes, which, in turn feeds the DataQuantum to each of its Genes.  Each Gene selects one or more DataPoints from the DataQuantum, operates on that data in some way, and then adds one or more new DataPoints to the DataQuantum, which is then passed down the chain.

After the DataQuantum has been fed to an Organism, it will have the original set of DataPoints as well as new DataPoints, at least one new DataPoint for each Gene in the Organism.

An Evaluator then scores the DataQuantum in some way.  The next DataQuantum is fed to the Organism, and the Evaluator scores that one as well.  After all of the DataQuanta in the time series have been fed to the Organism, it is given a score by the Evaluator.

## Experimentation

An Experiment has the following steps:

1. An OrganismRepository is seeded with Organisms.
2. All of the Organisms in the OrganismRepository are evaluated by a singe Evaluator.  The Score for each Organism are stored in a ScoreRepository.
3. The Experiment selects two Organisms from the OrganismRepository, breeds them, mutates the children, evaluates the children, and stores the children in the OrganismRepository and the Scores for the children in the ScoreRepository.
4. This process continues for as long as the Experiment is intended to run.

## Interpretation and Example

As an example, consider the time-series data of open, high, low, and close prices for the Dow Jones Industrial Average index.  This is a well-known set of data and many people try to find an effective way to model this data since that would presumably enable them to make a lot of money in the stock market.

We can evaluate an Organism by feeding it each of the data points in that time series. After the Organism consumes a DataQuantum, the DataQuantum will have many more DataPoints than the first four it was initialized with (open, high, low, close).

If we interpret the last DataPoint in the DataQuantum as a prediction for the open price in two days, and then feed the Organism two more DataQuanta, then we can start evaluating the DataQuanta by the absolute difference between the fist DataPoint in a DataQuantum and the last DataPoint in the DataQuantum that came two times previous in the series.