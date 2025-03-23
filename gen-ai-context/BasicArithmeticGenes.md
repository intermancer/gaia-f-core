# Basic Arithmetic Genes

**Basic Arithmetic Genes** apply basic math functions to one or more DataPoints. (DataPoint is a public inner class of DataQuantum.)

The base project for the Basic Arithmetic Genes is com.intermancer.gaiaf.core.organism.gene.basic.

## Single-DataPoint Genes

A **Single-DataPoint Gene** uses a single, constant index to pull a DataPoint from the stream of DataQuanta.  It then applies a mathematical operation (probably using another constant value), creates a new DataPoint, with its own id property as sourceId, and adds this new DataPoint to the DataQuantum.

There are Single-DataPoint Genes for addition, subtraction, multiplication, division, exponential, and the trigonometric functions.

`BaseSingleDataPointGene.java` defines an abstract bsse class that includes:
- getters and setters for the index (it is constant during scoring, but could be set during mutation), and appliedConstant properties.
- a `toString()` method to override `Object.toString()` which includes the implementation class name, the index, and the appliedConstant.
- an implementation of `getId()` that returns the value of `toString()`.
- a declaration of `public double abstract operation(double dataPointValue);`
- a template implementation of `void consume(DataQuantum dataQuantum)` that
  - uses the index property to get a DataPoint from dataQuantum, called dataPoint
  - creates a new DataPoint using `new DataPoint(this.getId(), operation(dataPoint.getValue()))`
  - adds the new DataPoint to dataQuantum.