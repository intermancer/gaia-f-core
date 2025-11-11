import React, { useState, useEffect } from 'react';
import './MainContent.css';

interface MainContentProps {
  selectedCommand: string | null;
}

const MainContent: React.FC<MainContentProps> = ({ selectedCommand }) => {
  const [experimentStatus, setExperimentStatus] = useState<string>('No experiment running');
  const [isRunning, setIsRunning] = useState<boolean>(false);

  // Use useEffect to handle side effects when commands are clicked
  useEffect(() => {
    if (selectedCommand === 'Start Experiment') {
      handleStartExperiment();
    } else if (selectedCommand === 'Stop Experiment') {
      handleStopExperiment();
    }
  }, [selectedCommand]);

  const handleConfigureExperiment = () => {
    // TODO: Implement configuration UI
    return (
      <div className="configure-view">
        <h1>Configure Experiment</h1>
        <div className="status-section">
          <h2>Configuration</h2>
          <p>Experiment configuration options will be displayed here.</p>
        </div>
      </div>
    );
  };

  const handleStartExperiment = async () => {
    try {
      // TODO: Replace with actual API call to /gaia-f/experiment/start
      setExperimentStatus('Starting experiment...');
      setIsRunning(true);
      
      // Simulated API call
      setTimeout(() => {
        setExperimentStatus('Experiment is running');
      }, 1000);
    } catch (error) {
      setExperimentStatus(`Error: ${error}`);
    }
  };

  const handleStopExperiment = async () => {
    try {
      // TODO: Replace with actual API call to /gaia-f/experiment/stop
      setExperimentStatus('Stopping experiment...');
      
      // Simulated API call
      setTimeout(() => {
        setExperimentStatus('Experiment stopped');
        setIsRunning(false);
      }, 1000);
    } catch (error) {
      setExperimentStatus(`Error: ${error}`);
    }
  };

  const renderContent = () => {
    if (!selectedCommand) {
      return (
        <div className="welcome">
          <h1>Welcome to GAIA-F Core</h1>
          <p>Select a command from the sidebar to get started.</p>
        </div>
      );
    }

    switch (selectedCommand) {
      case 'Experiment':
        return (
          <div className="experiment-view">
            <h1>Experiment</h1>
            <div className="status-section">
              <h2>Status</h2>
              <div className={`status-badge ${isRunning ? 'running' : 'stopped'}`}>
                {experimentStatus}
              </div>
            </div>
          </div>
        );
      
      case 'Configure Experiment':
        return handleConfigureExperiment();
      
      case 'Start Experiment':
        return (
          <div className="experiment-view">
            <h1>Start Experiment</h1>
            <div className="status-section">
              <h2>Status</h2>
              <div className={`status-badge ${isRunning ? 'running' : 'stopped'}`}>
                {experimentStatus}
              </div>
            </div>
          </div>
        );
      
      case 'Stop Experiment':
        return (
          <div className="experiment-view">
            <h1>Stop Experiment</h1>
            <div className="status-section">
              <h2>Status</h2>
              <div className={`status-badge ${isRunning ? 'running' : 'stopped'}`}>
                {experimentStatus}
              </div>
            </div>
          </div>
        );
      
      case 'Scored Organism Repository':
        return (
          <div className="repository-view">
            <h1>Scored Organism Repository</h1>
            <div className="status-section">
              <h2>Repository Information</h2>
              <p>Select an option from the sidebar to view scored organisms.</p>
            </div>
          </div>
        );
      
      case 'List Scored Organisms':
        return (
          <div className="repository-view">
            <h1>List Scored Organisms</h1>
            <div className="status-section">
              <h2>All Scored Organisms</h2>
              <p>List of all scored organisms will be displayed here.</p>
              {/* TODO: Implement API call to fetch and display organisms */}
            </div>
          </div>
        );
      
      case 'Display Top 5 Scored Organisms':
        return (
          <div className="repository-view">
            <h1>Top 5 Scored Organisms</h1>
            <div className="status-section">
              <h2>Top Performers</h2>
              <p>Top 5 scored organisms will be displayed here.</p>
              {/* TODO: Implement API call to fetch and display top 5 organisms */}
            </div>
          </div>
        );
      
      default:
        return (
          <div className="default-view">
            <h1>{selectedCommand}</h1>
            <p>Content for {selectedCommand} will be displayed here.</p>
          </div>
        );
    }
  };

  return (
    <div className="main-content">
      {renderContent()}
    </div>
  );
};

export default MainContent;