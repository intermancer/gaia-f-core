package com.intermancer.gaiaf.core.experiment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

/**
 * Test class for BasicExperimentImpl.
 * Verifies that the experiment orchestration correctly seeds the repository
 * and executes the configured number of experiment cycles with proper experimentId tracking.
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
        String experimentId = basicExperiment.getId();
        assertNotNull(experimentId);
        verify(seeder, times(1)).seed(experimentId);
        verify(experimentCycle, times(expectedCycleCount)).mutationCycle(experimentId);
        assertEquals(expectedCycleCount, experimentStatus.getCyclesCompleted());
        assertEquals(ExperimentState.STOPPED, experimentStatus.getStatus());
    }
    
    @Test
    void testGetId_returnsNonNullUuid() {
        // Act
        String experimentId = basicExperiment.getId();
        
        // Assert
        assertNotNull(experimentId);
        assertFalse(experimentId.isEmpty());
        // Verify it looks like a UUID (contains hyphens in UUID format)
        assertTrue(experimentId.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }
    
    @Test
    void testGetId_consistentAcrossMultipleCalls() {
        // Act
        String experimentId1 = basicExperiment.getId();
        String experimentId2 = basicExperiment.getId();
        
        // Assert
        assertEquals(experimentId1, experimentId2);
    }
    
    @Test
    void testRunExperiment_passesExperimentIdToSeeder() {
        // Arrange
        when(experimentConfiguration.getCycleCount()).thenReturn(1);
        ArgumentCaptor<String> experimentIdCaptor = ArgumentCaptor.forClass(String.class);
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert
        verify(seeder).seed(experimentIdCaptor.capture());
        String capturedExperimentId = experimentIdCaptor.getValue();
        assertEquals(basicExperiment.getId(), capturedExperimentId);
    }
    
    @Test
    void testRunExperiment_passesExperimentIdToExperimentCycle() {
        // Arrange
        when(experimentConfiguration.getCycleCount()).thenReturn(3);
        ArgumentCaptor<String> experimentIdCaptor = ArgumentCaptor.forClass(String.class);
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert
        verify(experimentCycle, times(3)).mutationCycle(experimentIdCaptor.capture());
        // Verify all calls used the same experimentId
        for (String capturedId : experimentIdCaptor.getAllValues()) {
            assertEquals(basicExperiment.getId(), capturedId);
        }
    }
    
    @Test
    void testRunExperiment_withZeroCycles() {
        // Arrange
        when(experimentConfiguration.getCycleCount()).thenReturn(0);
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert
        String experimentId = basicExperiment.getId();
        verify(seeder, times(1)).seed(experimentId);
        verify(experimentCycle, never()).mutationCycle(anyString());
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
        String experimentId = basicExperiment.getId();
        verify(seeder, times(1)).seed(experimentId);
        verify(experimentCycle, times(1)).mutationCycle(experimentId);
        assertEquals(1, experimentStatus.getCyclesCompleted());
    }
    
    @Test
    void testRunExperiment_withDefaultCycleCount() {
        // Arrange - testing with default value of 100
        when(experimentConfiguration.getCycleCount()).thenReturn(100);
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert
        String experimentId = basicExperiment.getId();
        verify(seeder, times(1)).seed(experimentId);
        verify(experimentCycle, times(100)).mutationCycle(experimentId);
        assertEquals(100, experimentStatus.getCyclesCompleted());
    }
    
    @Test
    void testRunExperiment_seedingOccursBeforeCycles() {
        // Arrange
        when(experimentConfiguration.getCycleCount()).thenReturn(5);
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert - verify order of operations
        String experimentId = basicExperiment.getId();
        var inOrder = inOrder(seeder, experimentCycle);
        inOrder.verify(seeder).seed(experimentId);
        inOrder.verify(experimentCycle, times(5)).mutationCycle(experimentId);
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
        }).when(experimentCycle).mutationCycle(anyString());
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert
        verify(experimentCycle).mutationCycle(anyString());
    }
    
    @Test
    void testRunExperiment_setsStatusToExceptionOnError() {
        // Arrange
        when(experimentConfiguration.getCycleCount()).thenReturn(5);
        doThrow(new RuntimeException("Test exception"))
                .when(experimentCycle).mutationCycle(anyString());
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> basicExperiment.runExperiment());
        assertEquals(ExperimentState.EXCEPTION, experimentStatus.getStatus());
    }
    
    @Test
    void testRunExperiment_setsStatusToExceptionOnSeederError() {
        // Arrange
        doThrow(new RuntimeException("Seeder exception"))
                .when(seeder).seed(anyString());
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> basicExperiment.runExperiment());
        assertEquals(ExperimentState.EXCEPTION, experimentStatus.getStatus());
        // Verify cycles were never called after seeder failed
        verify(experimentCycle, never()).mutationCycle(anyString());
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
        }).when(experimentCycle).mutationCycle(anyString());
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert
        assertEquals(3, experimentStatus.getCyclesCompleted());
        verify(experimentStatus, times(3)).incrementCyclesCompleted();
    }
    
    @Test
    void testRunExperiment_sameExperimentIdUsedThroughoutRun() {
        // Arrange
        when(experimentConfiguration.getCycleCount()).thenReturn(5);
        ArgumentCaptor<String> seederIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> cycleIdCaptor = ArgumentCaptor.forClass(String.class);
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert
        verify(seeder).seed(seederIdCaptor.capture());
        verify(experimentCycle, times(5)).mutationCycle(cycleIdCaptor.capture());
        
        String seederExperimentId = seederIdCaptor.getValue();
        assertEquals(basicExperiment.getId(), seederExperimentId);
        
        // Verify all cycle calls used the same ID
        for (String cycleExperimentId : cycleIdCaptor.getAllValues()) {
            assertEquals(basicExperiment.getId(), cycleExperimentId);
        }
    }
}
