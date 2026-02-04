import React, { useState, useEffect, useRef } from 'react';

interface ExperimentConfiguration {
  cycleCount: number;
  repoCapacity: number;
  pausable: boolean;
  pauseCycles: number;
}

interface ExperimentStatusData {
  cyclesCompleted: number;
  organismsReplaced: number;
  status: 'STOPPED' | 'RUNNING' | 'PAUSED' | 'EXCEPTION';
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
  const [statusData, setStatusData] = useState<ExperimentStatusData | null>(null);
  const [hasExperimentRun, setHasExperimentRun] = useState<boolean>(false);
  const pollingIntervalRef = useRef<number | null>(null);

  useEffect(() => {
    // Always fetch configuration (component's configuration is available without an experiment ID)
    fetchConfiguration();
    
    // Only fetch status if we have an experiment ID
    if (experimentId) {
      fetchStatus();
    }
  }, [experimentId]);

   useEffect(() => {
     if (isRunning && experimentId) {
       // Start polling when experiment is running and we have an experiment ID
       pollingIntervalRef.current = window.setInterval(() => {
         fetchStatus();
         fetchConfiguration();
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
   }, [isRunning, experimentId]);

   const fetchStatus = async () => {
     // Only fetch status if we have an experiment ID
     if (!experimentId) {
       return;
     }

     console.log('Pinging status...');

     try {
       const response = await fetch(`http://localhost:8080/gaia-f/experiment/${experimentId}/status`);
      
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
  };

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

  const fetchConfiguration = async () => {
    try {
      setLoading(true);
      setError(null);
      
      // If we have an experiment ID, fetch the configuration for that specific experiment
      // Otherwise, fetch the component's current configuration
      const url = experimentId 
        ? `http://localhost:8080/gaia-f/experiment/${experimentId}/configuration`
        : 'http://localhost:8080/gaia-f/experiment/configuration';
      
      const response = await fetch(url);
      
      if (!response.ok) {
        // noinspection ExceptionCaughtLocallyJS
        throw new Error(`Failed to fetch configuration: ${response.statusText}`);
      }
      
      const data: ExperimentConfiguration = await response.json();
      setConfig(data);
      setEditedConfig(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error occurred');
    } finally {
      setLoading(false);
    }
  };

  const handleConfigChange = (field: keyof ExperimentConfiguration, value: number) => {
    if (editedConfig) {
      setEditedConfig({
        ...editedConfig,
        [field]: value
      });
    }
  };

  const handleSaveConfiguration = async () => {
    if (!editedConfig) return;

    try {
      setLoading(true);
      setError(null);

      // Always update the component configuration (not experiment-specific)
      const response = await fetch('http://localhost:8080/gaia-f/experiment/configuration', {
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
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error occurred');
    } finally {
      setLoading(false);
    }
  };

  const handleStartExperiment = () => {
    setHasExperimentRun(true);
    onStartExperiment();
    // Refresh configuration and status after starting
    setTimeout(() => {
      fetchConfiguration();
      fetchStatus();
    }, 1000);
  };

  const handlePauseExperiment = async () => {
    if (!experimentId) return;
    
    try {
      const response = await fetch(`http://localhost:8080/gaia-f/experiment/${experimentId}/pause`, {
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
      const response = await fetch(`http://localhost:8080/gaia-f/experiment/${experimentId}/resume`, {
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
          <button onClick={fetchConfiguration}>Retry</button>
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
                value={editedConfig.cycleCount}
                onChange={(e) => handleConfigChange('cycleCount', parseInt(e.target.value))}
                disabled={isRunning || statusData?.status === 'PAUSED'}
                min="1"
              />
            </div>
            
            <div className="config-field">
              <label htmlFor="repoCapacity">Repository Capacity:</label>
              <input
                id="repoCapacity"
                type="number"
                value={editedConfig.repoCapacity}
                onChange={(e) => handleConfigChange('repoCapacity', parseInt(e.target.value))}
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
                  value={editedConfig.pauseCycles}
                  onChange={(e) => handleConfigChange('pauseCycles', parseInt(e.target.value))}
                  disabled={isRunning || statusData?.status === 'PAUSED'}
                  min="0"
                />
              </div>
            )}

            {hasChanges() && !isRunning && (
              <div className="config-actions">
                <button onClick={handleSaveConfiguration} disabled={loading}>
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