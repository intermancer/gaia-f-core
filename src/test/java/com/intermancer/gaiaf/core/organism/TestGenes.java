package com.intermancer.gaiaf.core.organism;

/**
 * Utility class containing reusable test Gene implementations.
 */
public class TestGenes {
    
    /**
     * A Gene that adds a constant value to the input.
     */
    public static class AdderGene extends Gene {
        
        public AdderGene(double amount, int inputIndex, String id) {
            super();
            // Set the target index
            getTargetIndexList().clear();
            getTargetIndexList().add(inputIndex);
            
            // Set the operation constant
            getOperationConstantList().add(amount);
            
            // Set the ID
            setId(id);
        }
        
        @Override
        protected double[] operation(double[] values) {
            double constant = getOperationConstantList().get(0);
            return new double[] { values[0] + constant };
        }
    }
    
    /**
     * A Gene that multiplies the input by a constant value.
     */
    public static class MultiplierGene extends Gene {
        
        public MultiplierGene(double factor, int inputIndex, String id) {
            super();
            // Set the target index
            getTargetIndexList().clear();
            getTargetIndexList().add(inputIndex);
            
            // Set the operation constant
            getOperationConstantList().add(factor);
            
            // Set the ID
            setId(id);
        }
        
        @Override
        protected double[] operation(double[] values) {
            double constant = getOperationConstantList().get(0);
            return new double[] { values[0] * constant };
        }
    }
    
    /**
     * A Gene that squares the input value.
     */
    public static class SquareGene extends Gene {
        
        public SquareGene(int inputIndex, String id) {
            super();
            // Set the target index
            getTargetIndexList().clear();
            getTargetIndexList().add(inputIndex);
            
            // Set the ID
            setId(id);
        }
        
        @Override
        protected double[] operation(double[] values) {
            return new double[] { values[0] * values[0] };
        }
    }
    
    /**
     * A Gene that takes the square root of the input value.
     */
    public static class SquareRootGene extends Gene {
        
        public SquareRootGene(int inputIndex, String id) {
            super();
            // Set the target index
            getTargetIndexList().clear();
            getTargetIndexList().add(inputIndex);
            
            // Set the ID
            setId(id);
        }
        
        @Override
        protected double[] operation(double[] values) {
            return new double[] { Math.sqrt(values[0]) };
        }
    }
    
    /**
     * A Gene that explicitly sets the sourceId in the output DataPoint.
     */
    public static class SourceIdGene extends Gene {
        
        public SourceIdGene(int inputIndex, double factor, String id) {
            super();
            // Set the target index
            getTargetIndexList().clear();
            getTargetIndexList().add(inputIndex);
            
            // Set the operation constant
            getOperationConstantList().add(factor);
            
            // Set the ID
            setId(id);
        }
        
        @Override
        protected double[] operation(double[] values) {
            double factor = getOperationConstantList().get(0);
            return new double[] { values[0] * factor };
        }
    }
}