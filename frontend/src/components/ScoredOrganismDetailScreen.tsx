import React, { useEffect, useState, useCallback } from 'react';
import type { ScoredOrganism } from '../types/repository';
import './ScoredOrganismDetailScreen.css';

interface ScoredOrganismDetailScreenProps {
  scoredOrganismId: string;
  onBack: () => void;
  onBackToExperiments: () => void;
}

const ScoredOrganismDetailScreen: React.FC<ScoredOrganismDetailScreenProps> = ({
  scoredOrganismId,
  onBack,
  onBackToExperiments,
}) => {
  const [detail, setDetail] = useState<ScoredOrganism | null>(null);
  const [loading, setLoading] = useState(true);

  const fetchDetail = useCallback(async () => {
    try {
      setLoading(true);
      const url = `http://localhost:8080/gaia-f/experiment/scored-organism/${scoredOrganismId}`;
      const response = await fetch(url);

      if (!response.ok) {
        throw new Error(`Failed to fetch scored organism: ${response.statusText}`);
      }

      const data: ScoredOrganism = await response.json();
      setDetail(data);
    } catch (err) {
      console.error('Error fetching scored organism detail:', err);
      onBackToExperiments();
    } finally {
      setLoading(false);
    }
  }, [scoredOrganismId, onBackToExperiments]);

  useEffect(() => {
    fetchDetail();
  }, [fetchDetail]);

  if (loading) {
    return (
      <div className="scored-organism-detail-screen">
        <h1>Scored Organism Detail</h1>
        <p>Loading...</p>
      </div>
    );
  }

  if (!detail) {
    return (
      <div className="scored-organism-detail-screen">
        <h1>Scored Organism Detail</h1>
        <p className="error-message">Failed to load scored organism</p>
      </div>
    );
  }

  return (
    <div className="scored-organism-detail-screen">
      <div className="header-row">
        <h1>Scored Organism Detail</h1>
        <button className="back-button" onClick={onBack}>
          Back to Scored Organisms
        </button>
      </div>

      <div className="detail-section">
        <div className="detail-row">
          <label>UUID:</label>
          <span className="uuid-value">{detail.id}</span>
        </div>

        <div className="detail-row">
          <label>Score:</label>
          <span>{detail.score.toFixed(6)}</span>
        </div>
      </div>

      <div className="organism-section">
        <h2>Organism JSON</h2>
        <pre className="organism-json">{JSON.stringify(detail.organism, null, 2)}</pre>
      </div>
    </div>
  );
};

export default ScoredOrganismDetailScreen;
