package com.intermancer.gaiaf.core.organism;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class DataQuantumTest {
    
    @Test
    public void testAddDataPoint() {
        DataQuantum dataQuantum = new DataQuantum();
        DataQuantum.DataPoint dataPoint = new DataQuantum.DataPoint("source1", 10.5);
        dataQuantum.addDataPoint(dataPoint);
        
        assertEquals(10.5, dataQuantum.getDataPoint(0).getValue());
        assertEquals("source1", dataQuantum.getDataPoint(0).getSourceId());
    }
    
    @Test
    public void testAddDataPointNull() {
        DataQuantum dataQuantum = new DataQuantum();
        IllegalArgumentException excpetion = assertThrows(IllegalArgumentException.class, 
                () -> dataQuantum.addDataPoint(null));
        assertEquals("DataPoint cannot be null", excpetion.getMessage());
    }
    
    @Test
    public void testGetDataPointWithModulo() {
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(1.0);
        dataQuantum.addValue(2.0);
        dataQuantum.addValue(3.0);
        
        // Test normal indices
        assertEquals(1.0, dataQuantum.getDataPoint(0).getValue());
        assertEquals(2.0, dataQuantum.getDataPoint(1).getValue());
        assertEquals(3.0, dataQuantum.getDataPoint(2).getValue());
        
        // Test indices with modulo
        assertEquals(1.0, dataQuantum.getDataPoint(3).getValue());
        assertEquals(2.0, dataQuantum.getDataPoint(4).getValue());
        assertEquals(3.0, dataQuantum.getDataPoint(5).getValue());
        assertEquals(1.0, dataQuantum.getDataPoint(6).getValue());

        // Test negative indices
        assertEquals(3.0, dataQuantum.getDataPoint(-1).getValue());
        assertEquals(2.0, dataQuantum.getDataPoint(-2).getValue());
        assertEquals(1.0, dataQuantum.getDataPoint(-3).getValue());
    }
    
    @Test
    public void testGetDataPointEmptyList() {
        DataQuantum dataQuantum = new DataQuantum();
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
                () -> dataQuantum.getDataPoint(0));

        assertEquals("No DataPoints available", exception.getMessage());
    }
    
    @Test
    public void testAddValue() {
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(5.5);
        
        assertEquals(5.5, dataQuantum.getDataPoint(0).getValue());
        assertNull(dataQuantum.getDataPoint(0).getSourceId());
    }
    
    @Test
    public void testGetValue() {
        DataQuantum dataQuantum = new DataQuantum();
        dataQuantum.addValue(7.7);
        dataQuantum.addValue(8.8);
        
        assertEquals(7.7, dataQuantum.getValue(0));
        assertEquals(8.8, dataQuantum.getValue(1));
        
        // Test modulo behavior
        assertEquals(7.7, dataQuantum.getValue(2));
        assertEquals(8.8, dataQuantum.getValue(3));
    }
    
    @Test
    public void testDataPointConstructor() {
        DataQuantum.DataPoint dataPoint1 = new DataQuantum.DataPoint("source2", 15.5);
        assertEquals(15.5, dataPoint1.getValue());
        assertEquals("source2", dataPoint1.getSourceId());
        
        DataQuantum.DataPoint dataPoint2 = new DataQuantum.DataPoint(null, 20.0);
        assertEquals(20.0, dataPoint2.getValue());
        assertNull(dataPoint2.getSourceId());
    }
}
