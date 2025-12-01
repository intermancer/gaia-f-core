import React, { useState, useEffect } from 'react';

interface ExperimentConfiguration {
  cycleCount: number;
  repoCapacity: number;
}

interface ExperimentStatusViewProps {
  experimentStatus: string;
  isRunning: boolean;
  onStartExperiment: () => void;
  onStopExperiment: () => void;
}

const ExperimentStatusView: React.FC<ExperimentStatusViewProps> = ({ 
  experimentStatus, 
  isRunning,
  onStartExperiment,
  onStopExperiment
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

  const handleStartExperiment = () => {
    onStartExperiment();
    // Refresh configuration after starting to get any server-side updates
    setTimeout(() => fetchConfiguration(), 1000);
  };

  const hasChanges = () => {
    if (!config || !editedConfig) return false;
    return config.cycleCount !== editedConfig.cycleCount || 
           config.repoCapacity !== editedConfig.repoCapacity;
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
        <h2>Status</h2>
        <div className={`status-badge ${isRunning ? 'running' : 'stopped'}`}>
          {experimentStatus}
        </div>
      </div>

      <div className="button-section">
        <button 
          onClick={handleStartExperiment} 
          disabled={isRunning || hasChanges()}
          className="experiment-button start-button"
        >
          Start Experiment
        </button>
        <button 
          onClick={onStopExperiment} 
          disabled={!isRunning}
          className="experiment-button stop-button"
        >
          Stop Experiment
        </button>
        {hasChanges() && (
          <p className="warning-text">Save configuration changes before starting</p>
        )}
      </div>
    </div>
  );
};

export default ExperimentStatusView;