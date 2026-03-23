import React, { useState, useEffect, useRef, useCallback } from 'react';
import { API_BASE } from '../utils/api';
import type { ExperimentStatusData } from '../types/repository';

interface ExperimentConfiguration {
  cycleCount: number;
  repoCapacity: number;
  pausable: boolean;
  pauseCycles: number;
}

interface ExperimentStatusViewProps {
  experimentStatus: string;
  isRunning: boolean;
  experimentId: string | null;
  onStartExperiment: () => void;
  onStatusChange?: (isRunning: boolean, status: string) => void;
}

const ExperimentStatusView: React.FC<ExperimentStatusViewProps> = ({ 
  experimentStatus, 
  isRunning,
  experimentId,
  onStartExperiment,
  onStatusChange
}) => {
  const [config, setConfig] = useState<ExperimentConfiguration | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [editedConfig, setEditedConfig] = useState<ExperimentConfiguration | null>(null);
  // Raw string values for numeric inputs so the field can be cleared while typing
  const [inputValues, setInputValues] = useState<Record<string, string>>({});
  const [statusData, setStatusData] = useState<ExperimentStatusData | null>(null);
  const [hasExperimentRun, setHasExperimentRun] = useState<boolean>(false);
  const pollingIntervalRef = useRef<number | null>(null);
  // Ref so fetchStatus always reads the current experimentId regardless of when
  // its closure was created (avoids stale closure inside setInterval).
  const experimentIdRef = useRef<string | null>(experimentId);
  useEffect(() => { experimentIdRef.current = experimentId; }, [experimentId]);

   const fetchStatus = useCallback(async () => {
     // Read from ref so this function is never stale inside setInterval
     const currentExperimentId = experimentIdRef.current;
     if (!currentExperimentId) {
       return;
     }

     try {
       const response = await fetch(`${API_BASE}/experiment/${currentExperimentId}/status`);

       if (!response.ok) {
         // noinspection ExceptionCaughtLocallyJS
         throw new Error(`Failed to fetch status: ${response.statusText}`);
       }

       const data: ExperimentStatusData = await response.json();
       setStatusData(data);

       // Notify parent component of status changes
       if (onStatusChange) {
         const newIsRunning = data.status === 'RUNNING';
         const newStatusText = getStatusDisplayText(data);
         onStatusChange(newIsRunning, newStatusText);
       }
     } catch (err) {
       console.error('Error fetching status:', err);
     }
   }, [onStatusChange]);

  useEffect(() => {
    // Always fetch configuration (component's configuration is available without an experiment ID)
    fetchConfiguration();

    // Only fetch status if we have an experiment ID
    if (experimentId) {
      fetchStatus();
    }
  }, [experimentId, fetchStatus]);

   useEffect(() => {
     if (isRunning && experimentId) {
       // Start polling when experiment is running and we have an experiment ID.
       // fetchStatus reads experimentId from a ref so the interval never goes stale.
       pollingIntervalRef.current = window.setInterval(() => {
         fetchStatus();
         fetchConfiguration(true);
       }, 1000);
     } else {
       // Stop polling when experiment is not running or no experiment ID
       if (pollingIntervalRef.current) {
         clearInterval(pollingIntervalRef.current);
         pollingIntervalRef.current = null;
       }
     }

     return () => {
       if (pollingIntervalRef.current) {
         clearInterval(pollingIntervalRef.current);
       }
     };
   }, [isRunning, experimentId, fetchStatus]);

  const getStatusDisplayText = (data: ExperimentStatusData): string => {
    if (data.status === 'RUNNING') {
      return 'Experiment Running';
    } else if (data.status === 'PAUSED') {
      return 'Experiment Paused';
    } else if (data.status === 'EXCEPTION') {
      return 'Experiment Error';
    } else {
      return 'No Experiment Running';
    }
  };

  const fetchConfiguration = async (preserveEdits: boolean = false) => {
    try {
      setLoading(true);
      setError(null);
      
      // If we have an experiment ID, fetch the configuration for that specific experiment
      // Otherwise, fetch the component's current configuration
      const url = experimentId
        ? `${API_BASE}/experiment/${experimentId}/configuration`
        : `${API_BASE}/experiment/configuration`;
      
      const response = await fetch(url);
      
      if (!response.ok) {
        // noinspection ExceptionCaughtLocallyJS
        throw new Error(`Failed to fetch configuration: ${response.statusText}`);
      }
      
      const data: ExperimentConfiguration = await response.json();
      // Always update the read-only display config
      setConfig(data);
      // Only overwrite the editable form state when the experiment is not running,
      // to avoid clobbering in-progress user edits during polling.
      if (!preserveEdits) {
        setEditedConfig(data);
        setInputValues({
          cycleCount: String(data.cycleCount),
          repoCapacity: String(data.repoCapacity),
          pauseCycles: String(data.pauseCycles),
        });
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error occurred');
    } finally {
      setLoading(false);
    }
  };

  const handleNumericInputChange = (field: 'cycleCount' | 'repoCapacity' | 'pauseCycles', raw: string) => {
    // Always update the display string so the field can be freely edited (including cleared)
    setInputValues(prev => ({ ...prev, [field]: raw }));
    // Only commit to editedConfig when the value is a valid integer
    const parsed = parseInt(raw, 10);
    if (!isNaN(parsed) && editedConfig) {
      setEditedConfig({ ...editedConfig, [field]: parsed });
    }
  };

  const handleSaveConfiguration = async () => {
    if (!editedConfig) return;

    try {
      setLoading(true);
      setError(null);

      // Always update the component configuration (not experiment-specific)
      const response = await fetch(`${API_BASE}/experiment/configuration`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(editedConfig),
      });

      if (!response.ok) {
        // noinspection ExceptionCaughtLocallyJS
        throw new Error(`Failed to update configuration: ${response.statusText}`);
      }

      const updatedConfig: ExperimentConfiguration = await response.json();
      setConfig(updatedConfig);
      setEditedConfig(updatedConfig);
      setInputValues({
        cycleCount: String(updatedConfig.cycleCount),
        repoCapacity: String(updatedConfig.repoCapacity),
        pauseCycles: String(updatedConfig.pauseCycles),
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error occurred');
    } finally {
      setLoading(false);
    }
  };

  const handleStartExperiment = () => {
    setHasExperimentRun(true);
    onStartExperiment();
    // Refresh status after starting; preserve edits since the experiment is now running
    setTimeout(() => {
      fetchConfiguration(true);
      fetchStatus();
    }, 1000);
  };

  const handlePauseExperiment = async () => {
    if (!experimentId) return;
    
    try {
      const response = await fetch(`${API_BASE}/experiment/${experimentId}/pause`, {
        method: 'POST',
      });
      
      if (!response.ok) {
        throw new Error(`Failed to pause experiment: ${response.statusText}`);
      }
      
      // Refresh status after pausing
      fetchStatus();
    } catch (error) {
      console.error('Error pausing experiment:', error);
    }
  };

  const handleResumeExperiment = async () => {
    if (!experimentId) return;
    
    try {
      const response = await fetch(`${API_BASE}/experiment/${experimentId}/resume`, {
        method: 'POST',
      });
      
      if (!response.ok) {
        throw new Error(`Failed to resume experiment: ${response.statusText}`);
      }
      
      // Refresh status after resuming
      fetchStatus();
    } catch (error) {
      console.error('Error resuming experiment:', error);
    }
  };

  const hasInvalidInputs = () => {
    return isNaN(parseInt(inputValues.cycleCount, 10))
      || isNaN(parseInt(inputValues.repoCapacity, 10))
      || (editedConfig?.pausable && isNaN(parseInt(inputValues.pauseCycles, 10)));
  };

  const hasChanges = () => {
    if (!config || !editedConfig) return false;
    return config.cycleCount !== editedConfig.cycleCount ||
           config.repoCapacity !== editedConfig.repoCapacity ||
           config.pausable !== editedConfig.pausable ||
           config.pauseCycles !== editedConfig.pauseCycles;
  };

  if (loading && !config) {
    return (
      <div className="experiment-view">
        <h1>Experiment</h1>
        <p>Loading configuration...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="experiment-view">
        <h1>Experiment</h1>
        <div className="error-message">
          <p>Error: {error}</p>
          <button onClick={() => fetchConfiguration()}>Retry</button>
        </div>
      </div>
    );
  }

  return (
    <div className="experiment-view">
      <h1>Experiment</h1>
      
      <div className="status-section">
        <h2>Configuration</h2>
        {editedConfig && (
          <div className="config-form">
            <div className="config-field">
              <label htmlFor="cycleCount">Cycle Count:</label>
              <input
                id="cycleCount"
                type="number"
                value={inputValues.cycleCount ?? editedConfig.cycleCount}
                onChange={(e) => handleNumericInputChange('cycleCount', e.target.value)}
                disabled={isRunning || statusData?.status === 'PAUSED'}
                min="1"
              />
            </div>
            
            <div className="config-field">
              <label htmlFor="repoCapacity">Repository Capacity:</label>
              <input
                id="repoCapacity"
                type="number"
                value={inputValues.repoCapacity ?? editedConfig.repoCapacity}
                onChange={(e) => handleNumericInputChange('repoCapacity', e.target.value)}
                disabled={isRunning || statusData?.status === 'PAUSED'}
                min="1"
              />
            </div>
            
            <div className="config-field checkbox-field">
              <label htmlFor="pausable">
                <input
                  id="pausable"
                  type="checkbox"
                  checked={editedConfig.pausable}
                  onChange={(e) => setEditedConfig({ ...editedConfig, pausable: e.target.checked })}
                  disabled={isRunning || statusData?.status === 'PAUSED'}
                />
                <span>Pausable</span>
              </label>
            </div>
            
            {editedConfig.pausable && (
              <div className="config-field">
                <label htmlFor="pauseCycles">Pause Cycles:</label>
                <input
                  id="pauseCycles"
                  type="number"
                  value={inputValues.pauseCycles ?? editedConfig.pauseCycles}
                  onChange={(e) => handleNumericInputChange('pauseCycles', e.target.value)}
                  disabled={isRunning || statusData?.status === 'PAUSED'}
                  min="0"
                />
              </div>
            )}

            {hasChanges() && !isRunning && (
              <div className="config-actions">
                <button onClick={handleSaveConfiguration} disabled={loading || hasInvalidInputs()}>
                  {loading ? 'Saving...' : 'Save Configuration'}
                </button>
                <button onClick={() => setEditedConfig(config)}>
                  Reset
                </button>
              </div>
            )}
          </div>
        )}
      </div>

      <div className="status-section">
        <h2>Status</h2>
        <div className={`status-badge ${statusData?.status === 'RUNNING' ? 'running' : statusData?.status === 'PAUSED' ? 'paused' : 'stopped'}`}>
          {!experimentId ? 'No Experiment Running' : (statusData ? getStatusDisplayText(statusData) : experimentStatus)}
        </div>
        {statusData && (statusData.status === 'RUNNING' || statusData.status === 'PAUSED' || (statusData.status === 'STOPPED' && hasExperimentRun)) && (
          <div className="status-details">
            <p>Cycles Completed: {statusData.cyclesCompleted}</p>
            <p>Organisms Replaced: {statusData.organismsReplaced}</p>
          </div>
        )}
        {(isRunning || statusData?.status === 'PAUSED' || hasExperimentRun) && config && (
          <div className="status-details">
            <h3>Experiment Configuration</h3>
            <p>Cycle Count: {config.cycleCount}</p>
            <p>Repository Capacity: {config.repoCapacity}</p>
            {config.pausable && (
              <>
                <p>Pausable: Yes</p>
                <p>Pause Cycles: {config.pauseCycles}</p>
              </>
            )}
          </div>
        )}
      </div>

      <div className="button-section">
        <button 
          onClick={handleStartExperiment} 
          disabled={isRunning || statusData?.status === 'PAUSED' || hasChanges()}
          className="experiment-button start-button"
        >
          Start Experiment
        </button>
        
        {config?.pausable && statusData?.status === 'RUNNING' && (
          <button 
            onClick={handlePauseExperiment} 
            className="experiment-button pause-button"
          >
            Pause Experiment
          </button>
        )}
        
        {statusData?.status === 'PAUSED' && (
          <button 
            onClick={handleResumeExperiment} 
            className="experiment-button resume-button"
          >
            Resume Experiment
          </button>
        )}
        
        {hasChanges() && (
          <p className="warning-text">Save configuration changes before starting</p>
        )}
      </div>
    </div>
  );
};

export default ExperimentStatusView;