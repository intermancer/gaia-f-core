# Basic Arithmetic Genes

**Basic Arithmetic Genes** apply basic math functions to one or more DataPoints. (DataPoint is a public inner class of DataQuantum.)

The base package for Basic Arithmetic Genes is `com.intermancer.gaiaf.core.organism.gene.basic`.

## Single-DataPoint Genes

A **Single-DataPoint Gene** uses a single, constant index to pull a DataPoint from the stream of DataQuanta.  It then applies a mathematical operation, creates a new DataPoint, with its own id property as sourceId, and adds this new DataPoint to the DataQuantum.

There are Single-DataPoint Genes that require a constant, such as addition, subtraction, multiplication, division, and exponential.  These Genes initialize operationConstantList, declared in Gene, with a single value of 1.5.

There are also Genes that implement operations which do not need to use any constant, such as sine, tangent, and the logarithm.

## Multi-DataPoint Genes

A **Multi-DataPoint Gene** uses two indices in its targetIndexList to pull two DataPoints from the stream of DataQuanta. It then applies a mathematical operation to those two values, creates a new DataPoint with its own id property as sourceId, and adds this new DataPoint to the DataQuantum.

Multi-DataPoint Genes do not use operationConstantList. The two operands are the values of the two DataPoints identified by the first and second entries in targetIndexList. By default, targetIndexList is initialized with two entries: -2 and -1, meaning the Gene operates on the second-to-last and last DataPoints in the DataQuantum.

The Multi-DataPoint Genes are:

- **DataPointAdditionGene**: adds the second DataPoint value to the first.
- **DataPointSubtractionGene**: subtracts the second DataPoint value from the first.
- **DataPointMultiplicationGene**: multiplies the first DataPoint value by the second.
- **DataPointDivisionGene**: divides the first DataPoint value by the second. Outputs 0.0 if the divisor is zero.

# Window Genes

**Window Genes** operate on a rolling buffer of recent values from a single data channel. Rather than reading a single DataQuantum in isolation, a Window Gene maintains internal state across successive calls to `consume()`, accumulating the last N values of a chosen DataPoint index. Once the buffer has at least one value, the Gene applies its operation to the buffered values and appends the result as a new DataPoint to the current DataQuantum.

The base package for Window Genes is `com.intermancer.gaiaf.core.organism.gene.window`.

## WindowGene Base Class

**WindowGene** is an abstract base class that all Window Genes extend. It manages the rolling buffer and defines the contract for window operations.

WindowGene stores the window size N as the first (and only) entry in operationConstantList, initialized to a default of 5.0 (interpreted as an integer). This makes the window size mutable via the standard Gene mutation infrastructure — mutations that adjust operationConstantList will grow or shrink the window over evolutionary time.

WindowGene uses a single entry in targetIndexList (defaulting to -1) to identify which DataPoint channel to sample from each DataQuantum.

On each call to `consume()`, WindowGene:
1. Reads the value at the target index from the current DataQuantum.
2. Appends that value to an internal buffer (a fixed-size queue, dropping the oldest value when the buffer exceeds N).
3. Passes the buffer contents as a `double[]` to the abstract `windowOperation()` method.
4. Appends the result as a new DataPoint to the DataQuantum.

During the warm-up period (fewer than N values buffered), the Gene operates on however many values are currently available rather than skipping or emitting a sentinel.

The abstract method subclasses implement is:

`protected abstract double[] windowOperation(double[] windowValues)`

WindowGene overrides `getWarmingCycles()` to return the window size N (the first entry in operationConstantList, cast to int). This allows Chromosomes and Organisms to correctly report how many DataQuanta must be consumed before the window is fully populated and the Gene's output is meaningful.

WindowGene overrides `cloneProperties()` to deep-copy the internal buffer into the clone, so a copied Gene begins with the same accumulated state.

## Window Gene Implementations

### MovingAverageGene

Computes the arithmetic mean of the values in the buffer.

`result = sum(windowValues) / windowValues.length`

### MovingMedianGene

Computes the median of the values in the buffer. More robust to outliers than a moving average; useful for identifying true central tendency when spikes are present.

For an odd-length buffer, returns the middle value of the sorted buffer. For an even-length buffer, returns the average of the two middle values.

### StandardDeviationGene

Computes the population standard deviation of the values in the buffer.

`result = sqrt( sum( (x - mean)^2 ) / N )`

Outputs a volatility measure. Pairs naturally with MovingAverageGene — a downstream Gene can combine both outputs to normalize values or detect regime changes.

### MomentumGene

Computes the difference between the most recent value and the oldest value currently in the buffer.

`result = windowValues[last] - windowValues[first]`

Measures the net change over the window, giving a direct reading of trend direction and speed.

### DelayGene

Emits the oldest value in the buffer — that is, the value from N steps ago — rather than computing an aggregate.

`result = windowValues[first]`

Allows an organism to compare the current state of a channel against its own past, enabling pattern detection across time.

### LinearProjectionGene

Fits a least-squares linear regression line to the buffer values and projects it forward by one step.

`result = slope * (N + 1) + intercept`

Where slope and intercept are derived from the standard least-squares formulas treating buffer position as the x-axis and buffer values as y. Projects the current linear trend one step into the future.

### ZScoreGene

Normalizes the most recent value relative to the window's mean and standard deviation.

`result = (windowValues[last] - mean) / standardDeviation`

Makes the output scale-invariant across different price levels or measurement ranges. Returns 0.0 if the standard deviation of the window is zero.

### RangeGene

Computes the difference between the maximum and minimum values in the buffer.

`result = max(windowValues) - min(windowValues)`

A simple volatility measure; a large range indicates high variability, a small range indicates consolidation.

### ExponentialMovingAverageGene

Computes an exponentially weighted moving average using a decay factor alpha stored as the second entry in operationConstantList, initialized to 0.2. The first entry in operationConstantList remains the window size N (used only to size the buffer for warm-up purposes).

`ema = alpha * windowValues[last] + (1 - alpha) * previousEma`

Where `previousEma` is the EMA computed on the previous `consume()` call, stored as instance state. On the first call, `previousEma` is initialized to the first buffered value.

Because alpha is in operationConstantList, it mutates independently of the window size, giving evolution two separate parameters to tune.

# Control Genes

**Control Genes** use the values of DataPoints to control and modify the flow of information through an organism. Rather than applying a fixed mathematical transformation, a Control Gene uses one or more DataPoints as signals that determine how another DataPoint's value is routed, filtered, or selected. The output is always a new DataPoint appended to the DataQuantum.

The base package for Control Genes is `com.intermancer.gaiaf.core.organism.gene.control`.

Control Genes are the first category that naturally requires three indices in targetIndexList. The existing Gene base class and its mutation infrastructure already support arbitrary-length targetIndexList, so no architectural changes are needed. Genes that require three indices initialize targetIndexList with three entries: -3, -2, and -1.

## Control Gene Implementations

### ClampGene

Reads three DataPoints identified by targetIndexList: a value, a floor, and a ceiling. Outputs the value clamped to the range defined by the floor and ceiling DataPoints.

`result = min(ceiling, max(floor, value))`

Where the first index is the value, the second index is the floor, and the third index is the ceiling. By default, targetIndexList is initialized with three entries: -3, -2, and -1.

Evolution can independently tune which channels supply the value, floor, and ceiling, allowing the organism to discover which computed signals make useful dynamic bounds.

### ThresholdSwitchGene

Reads three DataPoints identified by targetIndexList: two candidate values and a control signal. Also uses a constant threshold stored in operationConstantList, initialized to 0.0. If the control signal exceeds the threshold, outputs the first candidate value; otherwise outputs the second.

`result = (control > threshold) ? value1 : value2`

Where the first index is value1, the second index is value2, and the third index is the control signal. By default, targetIndexList is initialized with three entries: -3, -2, and -1.

The threshold is in operationConstantList, making it mutable. This is the SignalSwitch referenced in Phase 4 of Design.md.

### AbsoluteValueGateGene

Reads two DataPoints identified by targetIndexList: a value and a gate signal. Also uses a constant threshold stored in operationConstantList, initialized to 1.0. If the absolute value of the gate signal exceeds the threshold, outputs the value unchanged; otherwise outputs 0.0.

`result = (|gate| > threshold) ? value : 0.0`

Where the first index is the value and the second index is the gate signal. By default, targetIndexList is initialized with two entries: -2 and -1.

Acts as a noise filter — small or insignificant gate signals suppress the output entirely, allowing only sufficiently strong signals to pass through.

### ScaledBlendGene

Reads three DataPoints identified by targetIndexList: two values and a blend weight. Uses the third DataPoint's value as the mix ratio, clamped to [0.0, 1.0], to compute a weighted average of the two values.

`result = weight * value1 + (1 - weight) * value2`

Where the first index is value1, the second index is value2, and the third index is the blend weight. By default, targetIndexList is initialized with three entries: -3, -2, and -1.

Unlike fixed-constant blending, the mix ratio is data-driven. Evolution can discover which channel most usefully controls the balance between two competing signals.

### SignGene

Reads one DataPoint identified by targetIndexList. Also uses a constant magnitude stored in operationConstantList, initialized to 1.0. Outputs +magnitude if the value is positive, -magnitude if the value is negative, and 0.0 if the value is exactly zero.

`result = (value > 0) ? magnitude : (value < 0) ? -magnitude : 0.0`

Converts a continuous signal into a discrete directional signal. Useful as a building block before a ThresholdSwitchGene or MagnitudeGateGene.

### MagnitudeGateGene

Reads two DataPoints identified by targetIndexList: a value and a polarity control. Multiplies the value by the sign of the polarity control signal.

`result = value * sign(control)`

Where sign(x) is +1.0 if x > 0, -1.0 if x < 0, and 0.0 if x == 0. The first index is the value and the second index is the polarity control. By default, targetIndexList is initialized with two entries: -2 and -1.

Allows an organism to conditionally invert a signal based on the direction of another channel — for example, flipping the sign of a momentum value based on the direction of a trend indicator.

### BoundedScaleGene

Reads two DataPoints identified by targetIndexList: a value and a scale factor. Also uses a constant bound stored in operationConstantList, initialized to 2.0. Clamps the scale factor to the range [-bound, +bound] before multiplying it by the value.

`result = value * clamp(scale, -bound, bound)`

Where the first index is the value and the second index is the scale factor. By default, targetIndexList is initialized with two entries: -2 and -1.

Prevents a runaway scale value from producing extreme outputs when the scale channel carries noisy or unbounded data.

### SelectMaxGene

Reads two DataPoints identified by targetIndexList and outputs the larger of the two values. Does not use operationConstantList. By default, targetIndexList is initialized with two entries: -2 and -1.

`result = max(value1, value2)`

A simple building block that allows an organism to always carry forward the more extreme of two computed signals.

### SelectMinGene

Reads two DataPoints identified by targetIndexList and outputs the smaller of the two values. Does not use operationConstantList. By default, targetIndexList is initialized with two entries: -2 and -1.

`result = min(value1, value2)`

The complement of SelectMaxGene. Useful for suppressing outliers or selecting the more conservative of two signals.
