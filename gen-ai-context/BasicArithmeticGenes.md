# Basic Arithmetic Genes

**Basic Arithmetic Genes** apply basic math functions to one or more DataPoints. (DataPoint is a public inner class of DataQuantum.)

The base project for the Basic Arithmetic Genes is com.intermancer.gaiaf.core.organism.gene.basic.

## Single-DataPoint Genes

A **Single-DataPoint Gene**, uses a single, constant index to pull a DataPoint from the stream of DataQuanta going past.  It then applies a mathematical operation (possibly using another constant value), creates a new DataPoint, with its own id property as sourceId, and adds this new DataPoint to the DataQuantum.

There are Single-DataPoint Genes for addition, subtraction, multiplication, division, exponential, and the trigonometric functions.