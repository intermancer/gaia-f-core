package com.intermancer.gaiaf.core.experiment;

import com.intermancer.gaiaf.core.experiment.repo.ExperimentStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    
    @Mock
    private ExperimentStatusRepository experimentStatusRepository;
    
    @InjectMocks
    private BasicExperimentImpl basicExperiment;

    @Test
    void testRunExperiment_seedsRepositoryAndRunsCycles() {
        // Arrange
        int expectedCycleCount = 10;
        when(experimentConfiguration.getCycleCount()).thenReturn(expectedCycleCount);
        ArgumentCaptor<ExperimentStatus> statusCaptor = ArgumentCaptor.forClass(ExperimentStatus.class);
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert
        String experimentId = basicExperiment.getId();
        assertNotNull(experimentId);
        verify(seeder, times(1)).seed(experimentId);
        verify(experimentCycle, times(expectedCycleCount)).mutationCycle(eq(experimentId), any(ExperimentStatus.class));
        verify(experimentStatusRepository).save(statusCaptor.capture());
        ExperimentStatus savedStatus = statusCaptor.getValue();
        assertEquals(expectedCycleCount, savedStatus.getCyclesCompleted());
        assertEquals(ExperimentState.STOPPED, savedStatus.getStatus());
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
        verify(experimentCycle, times(3)).mutationCycle(experimentIdCaptor.capture(), any(ExperimentStatus.class));
        // Verify all calls used the same experimentId
        for (String capturedId : experimentIdCaptor.getAllValues()) {
            assertEquals(basicExperiment.getId(), capturedId);
        }
    }
    
    @Test
    void testRunExperiment_withZeroCycles() {
        // Arrange
        when(experimentConfiguration.getCycleCount()).thenReturn(0);
        ArgumentCaptor<ExperimentStatus> statusCaptor = ArgumentCaptor.forClass(ExperimentStatus.class);
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert
        String experimentId = basicExperiment.getId();
        verify(seeder, times(1)).seed(experimentId);
        verify(experimentCycle, never()).mutationCycle(anyString(), any(ExperimentStatus.class));
        verify(experimentStatusRepository).save(statusCaptor.capture());
        ExperimentStatus savedStatus = statusCaptor.getValue();
        assertEquals(0, savedStatus.getCyclesCompleted());
        assertEquals(ExperimentState.STOPPED, savedStatus.getStatus());
    }
    
    @Test
    void testRunExperiment_withSingleCycle() {
        // Arrange
        when(experimentConfiguration.getCycleCount()).thenReturn(1);
        ArgumentCaptor<ExperimentStatus> statusCaptor = ArgumentCaptor.forClass(ExperimentStatus.class);
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert
        String experimentId = basicExperiment.getId();
        verify(seeder, times(1)).seed(experimentId);
        verify(experimentCycle, times(1)).mutationCycle(eq(experimentId), any(ExperimentStatus.class));
        verify(experimentStatusRepository).save(statusCaptor.capture());
        ExperimentStatus savedStatus = statusCaptor.getValue();
        assertEquals(1, savedStatus.getCyclesCompleted());
    }
    
    @Test
    void testRunExperiment_withDefaultCycleCount() {
        // Arrange - testing with default value of 100
        when(experimentConfiguration.getCycleCount()).thenReturn(100);
        ArgumentCaptor<ExperimentStatus> statusCaptor = ArgumentCaptor.forClass(ExperimentStatus.class);
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert
        String experimentId = basicExperiment.getId();
        verify(seeder, times(1)).seed(experimentId);
        verify(experimentCycle, times(100)).mutationCycle(eq(experimentId), any(ExperimentStatus.class));
        verify(experimentStatusRepository).save(statusCaptor.capture());
        ExperimentStatus savedStatus = statusCaptor.getValue();
        assertEquals(100, savedStatus.getCyclesCompleted());
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
        inOrder.verify(experimentCycle, times(5)).mutationCycle(eq(experimentId), any(ExperimentStatus.class));
    }
    
    @Test
    void testRunExperiment_resetsStatusBeforeStarting() {
        // Arrange
        when(experimentConfiguration.getCycleCount()).thenReturn(5);
        ArgumentCaptor<ExperimentStatus> statusCaptor = ArgumentCaptor.forClass(ExperimentStatus.class);
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert - status should have been created fresh for this experiment
        verify(experimentStatusRepository).save(statusCaptor.capture());
        ExperimentStatus savedStatus = statusCaptor.getValue();
        assertEquals(5, savedStatus.getCyclesCompleted());
        assertEquals(0, savedStatus.getOrganismsReplaced());
        assertEquals(ExperimentState.STOPPED, savedStatus.getStatus());
    }

    @Test
    void testRunExperiment_setsStatusToRunningDuringExecution() {
        // Arrange
        when(experimentConfiguration.getCycleCount()).thenReturn(1);
        ArgumentCaptor<ExperimentStatus> statusCaptor = ArgumentCaptor.forClass(ExperimentStatus.class);
        doAnswer(invocation -> {
            // Verify the saved status is RUNNING when the cycle is called
            verify(experimentStatusRepository).save(statusCaptor.capture());
            assertEquals(ExperimentState.RUNNING, statusCaptor.getValue().getStatus());
            return null;
        }).when(experimentCycle).mutationCycle(anyString(), any(ExperimentStatus.class));
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert
        verify(experimentCycle).mutationCycle(anyString(), any(ExperimentStatus.class));
    }
    
    @Test
    void testRunExperiment_setsStatusToExceptionOnError() {
        // Arrange
        when(experimentConfiguration.getCycleCount()).thenReturn(5);
        doThrow(new RuntimeException("Test exception"))
                .when(experimentCycle).mutationCycle(anyString(), any(ExperimentStatus.class));
        ArgumentCaptor<ExperimentStatus> statusCaptor = ArgumentCaptor.forClass(ExperimentStatus.class);
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> basicExperiment.runExperiment());
        verify(experimentStatusRepository).save(statusCaptor.capture());
        ExperimentStatus savedStatus = statusCaptor.getValue();
        assertEquals(ExperimentState.EXCEPTION, savedStatus.getStatus());
    }
    
    @Test
    void testRunExperiment_setsStatusToExceptionOnSeederError() {
        // Arrange
        doThrow(new RuntimeException("Seeder exception"))
                .when(seeder).seed(anyString());
        ArgumentCaptor<ExperimentStatus> statusCaptor = ArgumentCaptor.forClass(ExperimentStatus.class);
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> basicExperiment.runExperiment());
        verify(experimentStatusRepository).save(statusCaptor.capture());
        ExperimentStatus savedStatus = statusCaptor.getValue();
        assertEquals(ExperimentState.EXCEPTION, savedStatus.getStatus());
        // Verify cycles were never called after seeder failed
        verify(experimentCycle, never()).mutationCycle(anyString(), any(ExperimentStatus.class));
    }
    
    @Test
    void testRunExperiment_incrementsCyclesCompletedAfterEachCycle() {
        // Arrange
        when(experimentConfiguration.getCycleCount()).thenReturn(3);
        final int[] cycleCounter = {0};
        doAnswer(invocation -> {
            cycleCounter[0]++;
            return null;
        }).when(experimentCycle).mutationCycle(anyString(), any(ExperimentStatus.class));
        ArgumentCaptor<ExperimentStatus> statusCaptor = ArgumentCaptor.forClass(ExperimentStatus.class);
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert
        verify(experimentStatusRepository).save(statusCaptor.capture());
        ExperimentStatus savedStatus = statusCaptor.getValue();
        assertEquals(3, savedStatus.getCyclesCompleted());
    }
    
    @Test
    void testRunExperiment_sameExperimentIdUsedThroughoutRun() {
        // Arrange
        when(experimentConfiguration.getCycleCount()).thenReturn(5);
        ArgumentCaptor<String> seederIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> cycleIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ExperimentStatus> statusCaptor = ArgumentCaptor.forClass(ExperimentStatus.class);
        
        // Act
        basicExperiment.runExperiment();
        
        // Assert
        verify(seeder).seed(seederIdCaptor.capture());
        verify(experimentCycle, times(5)).mutationCycle(cycleIdCaptor.capture(), any(ExperimentStatus.class));
        verify(experimentStatusRepository).save(statusCaptor.capture());
        
        String seederExperimentId = seederIdCaptor.getValue();
        assertEquals(basicExperiment.getId(), seederExperimentId);
        
        // Verify all cycle calls used the same ID
        for (String cycleExperimentId : cycleIdCaptor.getAllValues()) {
            assertEquals(basicExperiment.getId(), cycleExperimentId);
        }
        
        // Verify saved status has the correct experiment ID
        ExperimentStatus savedStatus = statusCaptor.getValue();
        assertEquals(basicExperiment.getId(), savedStatus.getExperimentId());
    }
}
