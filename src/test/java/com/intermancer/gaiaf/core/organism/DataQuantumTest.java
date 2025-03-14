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
}