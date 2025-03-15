package com.intermancer.gaiaf.core.organism;

import java.util.ArrayList;
import java.util.List;

public class DataQuantum {
    private final List<DataPoint> dataPoints = new ArrayList<>();

    public static class DataPoint {
        private final String sourceId;
        private final double value;

        public DataPoint(String sourceId, double value) {
            if (sourceId == null) {
                throw new IllegalArgumentException("sourceId cannot be null");
            }
            this.sourceId = sourceId;
            this.value = value;
        }

        public String getSourceId() {
            return sourceId;
        }

        public double getValue() {
            return value;
        }
    }

    public void addDataPoint(DataPoint dataPoint) {
        if (dataPoint == null) {
            throw new IllegalArgumentException("DataPoint cannot be null");
        }
        dataPoints.add(dataPoint);
    }

    public DataPoint getDataPoint(int index) {
        if (dataPoints.isEmpty()) {
            throw new IllegalStateException("No DataPoints available");
        }
        return dataPoints.get(index % dataPoints.size());
    }

    public double getValue(int index) {
        return getDataPoint(index).getValue();
    }

    public void addValue(double value) {
        dataPoints.add(new DataPoint("default", value));
    }
}