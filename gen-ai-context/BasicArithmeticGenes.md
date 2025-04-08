# Basic Arithmetic Genes

**Basic Arithmetic Genes** apply basic math functions to one or more DataPoints. (DataPoint is a public inner class of DataQuantum.)

The base project for the Basic Arithmetic Genes is com.intermancer.gaiaf.core.organism.gene.basic.

## Single-DataPoint Genes

A **Single-DataPoint Gene** uses a single, constant index to pull a DataPoint from the stream of DataQuanta.  It then applies a mathematical operation, creates a new DataPoint, with its own id property as sourceId, and adds this new DataPoint to the DataQuantum.

There are Single-DataPoint Genes that require a constant, such as addition, subtraction, multiplication, division, and exponential.  These Genes initialize operationConstantList, declared in Gene, with a single value of 1.5.

There are also Genes that implement operations which do not need to use any constant, such as sine, tangent, and the logarithm.
