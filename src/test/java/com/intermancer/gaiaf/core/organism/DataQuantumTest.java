package com.intermancer.gaiaf.core.organism;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DataQuantumTest {
    @Test
    public void testAddAndGetValue() {
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addDataPoint(new DataQuantum.DataPoint("1.0", 1.0));
        dataQuantum.addDataPoint(new DataQuantum.DataPoint("2.0", 2.0));
        assertEquals(1.0, dataQuantum.getValue(0));
        assertEquals(2.0, dataQuantum.getValue(1));
        assertEquals(1.0, dataQuantum.getValue(2)); // Test mod behavior
    }

    @Test
    public void testAddDataPoint() {
        // Create a DataQuantum
        DataQuantum dataQuantum = new DataQuantum();

        // Add a DataPoint
        DataQuantum.DataPoint dataPoint = new DataQuantum.DataPoint("source-1", 5.0);
        dataQuantum.addDataPoint(dataPoint);

        // Verify the DataPoint was added
        assertEquals(5.0, dataQuantum.getDataPoint(0).getValue());
        assertEquals("source-1", dataQuantum.getDataPoint(0).getSourceId());
    }

    @Test
    public void testAddValue() {
        // Create a DataQuantum
        DataQuantum dataQuantum = new DataQuantum();

        // Add a value
        dataQuantum.addValue(10.0);

        // Verify the value was added with a null sourceId
        assertEquals(10.0, dataQuantum.getDataPoint(0).getValue());
        assertNull(dataQuantum.getDataPoint(0).getSourceId());
    }

    @Test
    public void testGetDataPointWithModulo() {
        // Create a DataQuantum
        DataQuantum dataQuantum = new DataQuantum();

        // Add multiple DataPoints
        dataQuantum.addValue(1.0);
        dataQuantum.addValue(2.0);
        dataQuantum.addValue(3.0);

        // Verify modulo behavior
        assertEquals(1.0, dataQuantum.getDataPoint(0).getValue());
        assertEquals(2.0, dataQuantum.getDataPoint(1).getValue());
        assertEquals(3.0, dataQuantum.getDataPoint(2).getValue());
        assertEquals(1.0, dataQuantum.getDataPoint(3).getValue()); // Modulo wraps around
    }
}