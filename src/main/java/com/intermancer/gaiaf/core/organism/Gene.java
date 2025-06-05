package com.intermancer.gaiaf.core.organism;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.intermancer.gaiaf.core.experiment.MutationCommand;
import com.intermancer.gaiaf.core.experiment.Mutational;

/**
 * Represents a Gene, which is a DataQuantumConsumer.
 * A Gene processes a DataQuantum by retrieving values, performing
 * operations on them, and adding new values back into the DataQuantum.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS, // Use the class of the type
    include = JsonTypeInfo.As.PROPERTY, // Include type info as a property
    property = "type" // The property name in JSON
)
public abstract class Gene implements DataQuantumConsumer, Mutational {
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
    
    public void setTargetIndexList(List<Integer> targetIndexList) {
        this.targetIndexList = targetIndexList;
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
    
    /**
     * Copies the common properties from this Gene to the clone Gene.
     * Creates new instances of lists to ensure deep cloning.
     *
     * @param clone The Gene to copy properties to
     */
    protected void cloneProperties(Gene clone) {
        // Generate a new unique ID for the clone unless preserving the original ID is required
        clone.setId(UUID.randomUUID().toString());
        
        // Clone targetIndexList
        List<Integer> clonedTargetIndexList = new ArrayList<>();
        for (Integer index : this.targetIndexList) {
            clonedTargetIndexList.add(index);
        }
        clone.setTargetIndexList(clonedTargetIndexList);
        
        // Clone operationConstantList
        List<Double> clonedOperationConstantList = new ArrayList<>();
        for (Double constant : this.operationConstantList) {
            clonedOperationConstantList.add(constant);
        }
        clone.setOperationConstantList(clonedOperationConstantList);
    }
    
    /**
     * Creates a clone of this Gene.
     * Each concrete subclass must implement this method to create
     * a new instance of the same type and clone its properties.
     *
     * @return A clone of this Gene
     */
    public abstract Gene copyOf();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Gene other = (Gene) obj;
        
        // Compare targetIndexList
        if (targetIndexList == null) {
            if (other.targetIndexList != null) return false;
        } else if (!targetIndexList.equals(other.targetIndexList)) {
            return false;
        }
        
        // Compare operationConstantList
        if (operationConstantList == null) {
            if (other.operationConstantList != null) return false;
        } else if (!operationConstantList.equals(other.operationConstantList)) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((targetIndexList == null) ? 0 : targetIndexList.hashCode());
        result = prime * result + ((operationConstantList == null) ? 0 : operationConstantList.hashCode());
        return result;
    }

 /**
     * Implements the Mutational interface. Returns a list of possible mutations 
     * that can be applied to this Gene.
     * 
     * @return List of MutationCommand objects
     */
    @Override
    public List<MutationCommand> getMutationCommandList() {
        List<MutationCommand> mutations = new ArrayList<>();
        Random random = new Random();
        
        // Mutations for targetIndexList
        for (int i = 0; i < targetIndexList.size(); i++) {
            final int index = i;
            
            // Adjust targetIndex up
            mutations.add(getTargetIndexUpMutationCommand(random, index));
            
            // Adjust targetIndex down
            mutations.add(getTargetIndexDownMutationCommand(random, index));
        }
        
        // Mutations for operationConstantList
        for (int i = 0; i < operationConstantList.size(); i++) {
            final int index = i;
            
            // Adjust constant up by percentage
            mutations.add(getOperationalConstantUpMutationCommand(random, index));
            
            // Adjust constant down by percentage
            mutations.add(getOperationalConstantDownMutationCommand(random, index));
        }
        
        return mutations;
    }

 private MutationCommand getTargetIndexDownMutationCommand(Random random, final int index) {
    return new MutationCommand() {
        @Override
        public void execute() {
            int adjustment = random.nextInt(5) + 1; // 1 to 5
            int newValue = targetIndexList.get(index) - adjustment;
            targetIndexList.set(index, newValue);
        }
        
        @Override
        public String getDescription() {
            return "Decrease targetIndex[" + index + "] by 1-5";
        }
    };
 }

 private MutationCommand getTargetIndexUpMutationCommand(Random random, final int index) {
    return new MutationCommand() {
        @Override
        public void execute() {
            int adjustment = random.nextInt(5) + 1; // 1 to 5
            int newValue = targetIndexList.get(index) + adjustment;
            targetIndexList.set(index, newValue);
        }
        
        @Override
        public String getDescription() {
            return "Increase targetIndex[" + index + "] by 1-5";
        }
    };
 }

    private MutationCommand getOperationalConstantUpMutationCommand(Random random, final int index) {
        return new MutationCommand() {
            @Override
            public void execute() {
                double percentage = (random.nextInt(20) + 1) / 100.0; // 1% to 20%
                double currentValue = operationConstantList.get(index);
                double newValue = currentValue * (1 + percentage);
                operationConstantList.set(index, newValue);
            }
            
            @Override
            public String getDescription() {
                return "Increase operationConstant[" + index + "] by 1-20%";
            }
        };
    }

    private MutationCommand getOperationalConstantDownMutationCommand(Random random, final int index) {
        return new MutationCommand() {
            @Override
            public void execute() {
                double percentage = (random.nextInt(20) + 1) / 100.0; // 1% to 20%
                double currentValue = operationConstantList.get(index);
                double newValue = currentValue * (1 - percentage);
                operationConstantList.set(index, newValue);
            }
            
            @Override
            public String getDescription() {
                return "Decrease operationConstant[" + index + "] by 1-20%";
            }
        };
    }
}