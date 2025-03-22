package com.intermancer.gaiaf.core.organism;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a DataQuantum, which contains an ordered list of DataPoints.
 * A DataQuantum provides methods to add and retrieve DataPoints, ensuring
 * compliance with the specification in OrganismDomainObjects.md.
 */
public class DataQuantum {
    private final List<DataPoint> dataPoints = new ArrayList<>();

    /**
     * Represents a single DataPoint within a DataQuantum.
     * Each DataPoint has a sourceId and a value.
     */
    public static class DataPoint {
        private final String sourceId;
        private final double value;

        /**
         * Constructs a DataPoint with the specified sourceId and value.
         *
         * @param sourceId The source ID of the DataPoint (can be null).
         * @param value    The value of the DataPoint.
         */
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
         * Retrieves the value of the DataPoint.
         *
         * @return The value of the DataPoint.
         */
        public double getValue() {
            return value;
        }
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
     * Retrieves a DataPoint at the specified index. Uses modulo to ensure
     * the index is always valid, even if it exceeds the list size.
     *
     * @param index The index of the DataPoint to retrieve.
     * @return The DataPoint at the specified index.
     * @throws IllegalStateException if the list of DataPoints is empty.
     */
    public DataPoint getDataPoint(int index) {
        if (dataPoints.isEmpty()) {
            throw new IllegalStateException("No DataPoints available");
        }
        return dataPoints.get(index % dataPoints.size());
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
}