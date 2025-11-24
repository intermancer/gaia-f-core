import React, { useState, useEffect } from 'react';

interface ExperimentConfiguration {
  cycleCount: number;
  repoCapacity: number;
}

interface StartExperimentScreenProps {
  experimentStatus: string;
  isRunning: boolean;
  onStatusChange: (status: string) => void;
  onRunningChange: (isRunning: boolean) => void;
}

const StartExperimentScreen: React.FC<StartExperimentScreenProps> = ({ 
  experimentStatus, 
  isRunning,
  onStatusChange,
  onRunningChange
}) => {
  const [config, setConfig] = useState<ExperimentConfiguration | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [editedConfig, setEditedConfig] = useState<ExperimentConfiguration | null>(null);

  useEffect(() => {
    fetchConfiguration();
  }, []);

  const fetchConfiguration = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const response = await fetch('http://localhost:8080/gaia-f/experiment/configuration');
      
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

  const handleStartExperiment = async () => {
    try {
      onStatusChange('Starting experiment...');
      onRunningChange(true);
      
      const response = await fetch('http://localhost:8080/gaia-f/experiment/start', {
        method: 'POST',
      });
      
      if (!response.ok) {
        // noinspection ExceptionCaughtLocallyJS
        throw new Error(`Failed to start experiment: ${response.statusText}`);
      }
      
      const result = await response.text();
      onStatusChange(result);
    } catch (error) {
      onStatusChange(`Error: ${error instanceof Error ? error.message : 'Unknown error'}`);
      onRunningChange(false);
    }
  };

  const hasChanges = () => {
    if (!config || !editedConfig) return false;
    return config.cycleCount !== editedConfig.cycleCount || 
           config.repoCapacity !== editedConfig.repoCapacity;
  };

  if (loading && !config) {
    return (
      <div className="experiment-view">
        <h1>Start Experiment</h1>
        <p>Loading configuration...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="experiment-view">
        <h1>Start Experiment</h1>
        <div className="error-message">
          <p>Error: {error}</p>
          <button onClick={fetchConfiguration}>Retry</button>
        </div>
      </div>
    );
  }

  return (
    <div className="experiment-view">
      <h1>Start Experiment</h1>
      
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
                disabled={isRunning}
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
                disabled={isRunning}
                min="1"
              />
            </div>

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
        <h2>Actions</h2>
        <button 
          onClick={handleStartExperiment} 
          disabled={isRunning || hasChanges()}
          className="start-button"
        >
          {isRunning ? 'Experiment Running...' : 'Start Experiment'}
        </button>
        {hasChanges() && (
          <p className="warning-text">Save configuration changes before starting</p>
        )}
      </div>

      <div className="status-section">
        <h2>Status</h2>
        <div className={`status-badge ${isRunning ? 'running' : 'stopped'}`}>
          {experimentStatus}
        </div>
      </div>
    </div>
  );
};

export default StartExperimentScreen;