import React, { useEffect, useState } from 'react';
import type { ExperimentSummary } from '../types/repository';
import { formatDateTime, truncateUuid } from '../utils/formatters';
import './ExperimentsListScreen.css';

interface ExperimentsListScreenProps {
  onExperimentSelect: (experimentId: string) => void;
}

const ExperimentsListScreen: React.FC<ExperimentsListScreenProps> = ({
  onExperimentSelect,
}) => {
  const [experiments, setExperiments] = useState<ExperimentSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchExperiments();
  }, []);

  const fetchExperiments = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8080/gaia-f/experiment/list');
      if (!response.ok) {
        throw new Error(`Failed to fetch experiments: ${response.statusText}`);
      }
      const data: ExperimentSummary[] = await response.json();
      setExperiments(data);
      setError(null);
    } catch (err) {
      console.error('Error fetching experiments:', err);
      setError(err instanceof Error ? err.message : 'Unknown error');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="experiments-list-screen">
        <h1>Experiments</h1>
        <p>Loading...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="experiments-list-screen">
        <h1>Experiments</h1>
        <p className="error-message">Error: {error}</p>
      </div>
    );
  }

  return (
    <div className="experiments-list-screen">
      <h1>Experiments</h1>
      {experiments.length === 0 ? (
        <p className="no-data-message">No data available</p>
      ) : (
        <table className="experiments-table">
          <thead>
            <tr>
              <th>UUID</th>
              <th>Date/Time</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {experiments.map((experiment) => (
              <tr
                key={experiment.id}
                onClick={() => onExperimentSelect(experiment.id)}
                className="clickable-row"
              >
                <td title={experiment.id}>{truncateUuid(experiment.id)}</td>
                <td>{formatDateTime(experiment.createdAt)}</td>
                <td className={`status-${experiment.status.toLowerCase()}`}>
                  {experiment.status}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default ExperimentsListScreen;
