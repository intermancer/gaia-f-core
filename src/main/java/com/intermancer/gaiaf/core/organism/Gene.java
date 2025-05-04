package com.intermancer.gaiaf.core.organism;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents a Gene, which is a DataQuantumConsumer.
 * A Gene processes a DataQuantum by retrieving values, performing
 * operations on them, and adding new values back into the DataQuantum.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, // Use the name of the type
    include = JsonTypeInfo.As.PROPERTY, // Include type info as a property
    property = "type" // The property name in JSON
)
public abstract class Gene implements DataQuantumConsumer {
    private String id;
    private List<Integer> targetIndexList;
    private List<Double> operationConstantList;
    
    public Gene() {
        // Initialize targetIndexList with -1
        targetIndexList = new ArrayList<>();
        targetIndexList.add(-1);
        
        // Initialize operationConstantList as empty
        operationConstantList = new ArrayList<>();
    }
    
    public List<Integer> getTargetIndexList() {
        return targetIndexList;
    }
    
    public List<Double> getOperationConstantList() {
        return operationConstantList;
    }
    
    public void setOperationConstantList(List<Double> operationConstantList) {
        this.operationConstantList = operationConstantList;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    @Override
    public String getId() {
        return id != null ? id : getClass().getSimpleName();
    }
    
    /**
     * Processes the given DataQuantum. A Gene retrieves values from the DataQuantum
     * based on targetIndexList, performs operations on them, and adds new values
     * back into the DataQuantum.
     *
     * @param dataQuantum The DataQuantum to process.
     */
    @Override
    public void consume(DataQuantum dataQuantum) {
        // Extract values from dataQuantum based on targetIndexList
        double[] values = new double[targetIndexList.size()];
        for (int i = 0; i < targetIndexList.size(); i++) {
            values[i] = dataQuantum.getValue(targetIndexList.get(i));
        }
        
        // Perform operation on values
        double[] results = operation(values);
        
        // Add results as new DataPoints to dataQuantum
        for (double result : results) {
            dataQuantum.addDataPoint(new DataQuantum.DataPoint(getId(), result));
        }
    }
    
    /**
     * Abstract method to define the operation performed on the input values.
     *
     * @param values The values extracted from DataQuantum.
     * @return The results of the operation.
     */
    protected abstract double[] operation(double[] values);
}