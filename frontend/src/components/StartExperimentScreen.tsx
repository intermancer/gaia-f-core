import React from 'react';

interface StartExperimentScreenProps {
  experimentStatus: string;
  isRunning: boolean;
}

const StartExperimentScreen: React.FC<StartExperimentScreenProps> = ({ experimentStatus, isRunning }) => {
  return (
    <div className="experiment-view">
      <h1>Start Experiment</h1>
      <div className="status-section">
        <h2>Configuration</h2>
        <p>Experiment configuration options will be displayed here.</p>
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