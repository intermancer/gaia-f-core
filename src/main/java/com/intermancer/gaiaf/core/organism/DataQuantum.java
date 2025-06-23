package com.intermancer.gaiaf.core.organism;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Represents a DataQuantum, which contains an ordered list of DataPoints.
 * A DataQuantum provides methods to add and retrieve DataPoints, ensuring
 * compliance with the specification in OrganismDomainObjects.md.
 */
public class DataQuantum {
    private List<DataPoint> dataPoints;

    /**
     * Default constructor for Jackson deserialization.
     */
    public DataQuantum() {
        this.dataPoints = new ArrayList<>();
    }

    /**
     * Gets the list of DataPoints in this DataQuantum.
     *
     * @return The list of DataPoints
     */
    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

    /**
     * Sets the list of DataPoints for this DataQuantum.
     *
     * @param dataPoints The list of DataPoints to set
     */
    public void setDataPoints(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    /**
     * Adds a DataPoint to the DataQuantum. Null values are not allowed.
     *
     * @param dataPoint The DataPoint to add.
     * @throws IllegalArgumentException if the DataPoint is null.
     */
    public void addDataPoint(DataPoint dataPoint) {
        if (dataPoint == null) {
            throw new IllegalArgumentException("DataPoint cannot be null");
        }
        dataPoints.add(dataPoint);
    }

    /**
     * Retrieves a DataPoint at the specified index using modulo arithmetic to ensure safe array access. 
     * This method guarantees that a valid DataPoint is always returned as long as the DataQuantum 
     * contains at least one DataPoint.
     *
     * Index Behavior:
     * - Positive indices: Standard array indexing (0, 1, 2, etc.)
     * - Negative indices: Reverse indexing where -1 returns the last DataPoint, -2 returns the second-to-last, etc.
     * - Out-of-bounds indices: Automatically wrapped using modulo operation to stay within valid range
     *
     * Examples:
     * For a DataQuantum with 3 DataPoints (indices 0, 1, 2):
     * - getDataPoint(0) returns the first DataPoint
     * - getDataPoint(3) returns the first DataPoint (3 % 3 = 0)
     * - getDataPoint(-1) returns the last DataPoint
     * - getDataPoint(5) returns the third DataPoint (5 % 3 = 2)
     *
     * @param index The index of the DataPoint to retrieve.
     * @return The DataPoint at the specified index.
     * @throws IllegalStateException if the list of DataPoints is empty.
     */
    public DataPoint getDataPoint(int index) {
        if (dataPoints.isEmpty()) {
            throw new IllegalStateException("No DataPoints available");
        }
        
        int size = dataPoints.size();
        int actualIndex;
        
        if (index >= 0) {
            // Positive index: use modulo for wrapping
            actualIndex = index % size;
        } else {
            // Negative index: convert to positive equivalent
            // -1 becomes size-1, -2 becomes size-2, etc.
            // Handle cases where |index| > size using modulo
            actualIndex = ((index % size) + size) % size;
        }
        
        return dataPoints.get(actualIndex);
    }

    /**
     * Adds a new DataPoint with the specified value and a null sourceId.
     *
     * @param value The value of the new DataPoint.
     */
    public void addValue(double value) {
        dataPoints.add(new DataPoint(null, value));
    }

    /**
     * Retrieves the value of a DataPoint at the specified index.
     * This is a convenience method for getDataPoint(index).getValue().
     *
     * @param index The index of the DataPoint.
     * @return The value of the DataPoint at the specified index.
     */
    public double getValue(int index) {
        return getDataPoint(index).getValue();
    }

    /**
     * Represents a single DataPoint within a DataQuantum.
     * Each DataPoint has a sourceId and a value.
     */
    public static class DataPoint {
        private String sourceId;
        private double value;

        /**
         * Default constructor for Jackson deserialization.
         */
        public DataPoint() {
        }

        /**
         * Constructs a DataPoint with the specified sourceId and value.
         *
         * @param sourceId The source ID of the DataPoint (can be null).
         * @param value    The value of the DataPoint.
         */
        @JsonCreator
        public DataPoint(String sourceId, double value) {
            this.sourceId = sourceId;
            this.value = value;
        }

        /**
         * Retrieves the source ID of the DataPoint.
         *
         * @return The source ID (can be null).
         */
        public String getSourceId() {
            return sourceId;
        }

        /**
         * Sets the source ID of the DataPoint.
         *
         * @param sourceId The source ID to set (can be null).
         */
        public void setSourceId(String sourceId) {
            this.sourceId = sourceId;
        }

        /**
         * Retrieves the value of the DataPoint.
         *
         * @return The value of the DataPoint.
         */
        public double getValue() {
            return value;
        }

        /**
         * Sets the value of the DataPoint.
         *
         * @param value The value to set.
         */
        public void setValue(double value) {
            this.value = value;
        }
    }
}