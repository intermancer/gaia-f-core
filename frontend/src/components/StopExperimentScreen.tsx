import React from 'react';

interface StopExperimentScreenProps {
  experimentStatus: string;
  isRunning: boolean;
}

const StopExperimentScreen: React.FC<StopExperimentScreenProps> = ({ experimentStatus, isRunning }) => {
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
};

export default StopExperimentScreen;