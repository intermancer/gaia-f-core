import React from 'react';

interface ExperimentStatusViewProps {
  experimentStatus: string;
  isRunning: boolean;
}

const ExperimentStatusView: React.FC<ExperimentStatusViewProps> = ({ experimentStatus, isRunning }) => {
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
};

export default ExperimentStatusView;