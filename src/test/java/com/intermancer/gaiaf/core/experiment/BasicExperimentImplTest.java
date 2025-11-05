package com.intermancer.gaiaf.core.experiment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Test class for BasicExperimentImpl.
 * Verifies that the experiment orchestration correctly seeds the repository
 * and executes the configured number of experiment cycles.
 */
@ExtendWith(MockitoExtension.class)
class BasicExperimentImplTest {
    
    @Mock
    private Seeder seeder;
    
    @Mock
    private ExperimentConfiguration experimentConfiguration;
    
    @Mock
    private ExperimentCycle experimentCycle;
    
    @InjectMocks
    private BasicExperimentImpl basicExperiment;

    @Test
    void testRunExperiment_seedsRepositoryAndRunsCycles() {
        // Arrange
        int expectedCycleCount = 10;
        when(experimentConfiguration.getCycleCount()).thenReturn(expectedCycleCount);
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert
        verify(seeder, times(1)).seed();
        verify(experimentCycle, times(expectedCycleCount)).mutationCycle();
    }
    
    @Test
    void testRunExperiment_withZeroCycles() {
        // Arrange
        when(experimentConfiguration.getCycleCount()).thenReturn(0);
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert
        verify(seeder, times(1)).seed();
        verify(experimentCycle, never()).mutationCycle();
    }
    
    @Test
    void testRunExperiment_withSingleCycle() {
        // Arrange
        when(experimentConfiguration.getCycleCount()).thenReturn(1);
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert
        verify(seeder, times(1)).seed();
        verify(experimentCycle, times(1)).mutationCycle();
    }
    
    @Test
    void testRunExperiment_withDefaultCycleCount() {
        // Arrange - testing with default value of 100
        when(experimentConfiguration.getCycleCount()).thenReturn(100);
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert
        verify(seeder, times(1)).seed();
        verify(experimentCycle, times(100)).mutationCycle();
    }
    
    @Test
    void testRunExperiment_seedingOccursBeforeCycles() {
        // Arrange
        when(experimentConfiguration.getCycleCount()).thenReturn(5);
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert - verify order of operations
        var inOrder = inOrder(seeder, experimentCycle);
        inOrder.verify(seeder).seed();
        inOrder.verify(experimentCycle, times(5)).mutationCycle();
    }
}
