package com.intermancer.gaiaf.core.organism;

/**
 * Utility class containing reusable test Gene implementations.
 */
public class TestGenes {
    
    /**
     * A Gene that adds a constant value to the input.
     */
    public static class AdderGene extends Gene {
        private final double amount;
        private final int inputIndex;
        private final String id;
        
        public AdderGene(double amount, int inputIndex, String id) {
            this.amount = amount;
            this.inputIndex = inputIndex;
            this.id = id;
        }
        
        @Override
        public void consume(DataQuantum dataQuantum) {
            double value = dataQuantum.getValue(inputIndex);
            dataQuantum.addValue(value + amount);
        }
        
        @Override
        public String getId() {
            return id;
        }
    }
    
    /**
     * A Gene that multiplies the input by a constant value.
     */
    public static class MultiplierGene extends Gene {
        private final double factor;
        private final int inputIndex;
        private final String id;
        
        public MultiplierGene(double factor, int inputIndex, String id) {
            this.factor = factor;
            this.inputIndex = inputIndex;
            this.id = id;
        }
        
        @Override
        public void consume(DataQuantum dataQuantum) {
            double value = dataQuantum.getValue(inputIndex);
            dataQuantum.addValue(value * factor);
        }
        
        @Override
        public String getId() {
            return id;
        }
    }
    
    /**
     * A Gene that squares the input value.
     */
    public static class SquareGene extends Gene {
        private final int inputIndex;
        private final String id;
        
        public SquareGene(int inputIndex, String id) {
            this.inputIndex = inputIndex;
            this.id = id;
        }
        
        @Override
        public void consume(DataQuantum dataQuantum) {
            double value = dataQuantum.getValue(inputIndex);
            dataQuantum.addValue(value * value);
        }
        
        @Override
        public String getId() {
            return id;
        }
    }
    
    /**
     * A Gene that takes the square root of the input value.
     */
    public static class SquareRootGene extends Gene {
        private final int inputIndex;
        private final String id;
        
        public SquareRootGene(int inputIndex, String id) {
            this.inputIndex = inputIndex;
            this.id = id;
        }
        
        @Override
        public void consume(DataQuantum dataQuantum) {
            double value = dataQuantum.getValue(inputIndex);
            dataQuantum.addValue(Math.sqrt(value));
        }
        
        @Override
        public String getId() {
            return id;
        }
    }
    
    /**
     * A Gene that explicitly sets the sourceId in the output DataPoint.
     */
    public static class SourceIdGene extends Gene {
        private final int inputIndex;
        private final double factor;
        private final String id;
        
        public SourceIdGene(int inputIndex, double factor, String id) {
            this.inputIndex = inputIndex;
            this.factor = factor;
            this.id = id;
        }
        
        @Override
        public void consume(DataQuantum dataQuantum) {
            double value = dataQuantum.getValue(inputIndex);
            dataQuantum.addDataPoint(new DataQuantum.DataPoint(getId(), value * factor));
        }
        
        @Override
        public String getId() {
            return id;
        }
    }
}
