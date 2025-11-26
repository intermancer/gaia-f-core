package com.intermancer.gaiaf.core.experiment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
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
    
    @Spy
    private ExperimentStatus experimentStatus = new ExperimentStatus();
    
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
        assertEquals(expectedCycleCount, experimentStatus.getCyclesCompleted());
        assertEquals(ExperimentState.STOPPED, experimentStatus.getStatus());
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
        assertEquals(0, experimentStatus.getCyclesCompleted());
        assertEquals(ExperimentState.STOPPED, experimentStatus.getStatus());
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
        assertEquals(1, experimentStatus.getCyclesCompleted());
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
        assertEquals(100, experimentStatus.getCyclesCompleted());
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
    
    @Test
    void testRunExperiment_resetsStatusBeforeStarting() {
        // Arrange
        when(experimentConfiguration.getCycleCount()).thenReturn(5);
        experimentStatus.setCyclesCompleted(50);
        experimentStatus.setOrganismsReplaced(100);
        experimentStatus.setStatus(ExperimentState.EXCEPTION);
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert - status should have been reset and then updated
        assertEquals(5, experimentStatus.getCyclesCompleted());
        assertEquals(0, experimentStatus.getOrganismsReplaced());
        assertEquals(ExperimentState.STOPPED, experimentStatus.getStatus());
    }

    @Test
    void testRunExperiment_setsStatusToRunningDuringExecution() {
        // Arrange
        when(experimentConfiguration.getCycleCount()).thenReturn(1);
        doAnswer(invocation -> {
            assertEquals(ExperimentState.RUNNING, experimentStatus.getStatus());
            return null;
        }).when(experimentCycle).mutationCycle();
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert
        verify(experimentCycle).mutationCycle();
    }
    
    @Test
    void testRunExperiment_setsStatusToExceptionOnError() {
        // Arrange
        when(experimentConfiguration.getCycleCount()).thenReturn(5);
        doThrow(new RuntimeException("Test exception")).when(experimentCycle).mutationCycle();
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> basicExperiment.runExperiment());
        assertEquals(ExperimentState.EXCEPTION, experimentStatus.getStatus());
    }
    
    @Test
    void testRunExperiment_incrementsCyclesCompletedAfterEachCycle() {
        // Arrange
        when(experimentConfiguration.getCycleCount()).thenReturn(3);
        final int[] cycleCounter = {0};
        doAnswer(invocation -> {
            cycleCounter[0]++;
            // After the cycle completes and incrementCyclesCompleted is called,
            // the count should match the number of cycles run so far
            return null;
        }).when(experimentCycle).mutationCycle();
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert
        assertEquals(3, experimentStatus.getCyclesCompleted());
        verify(experimentStatus, times(3)).incrementCyclesCompleted();
    }
}
