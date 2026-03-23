import React, { useEffect, useState, useCallback } from 'react';
import { API_BASE } from '../utils/api';
import type { ScoredOrganismSummary, PaginatedResponse, ExperimentStatusData } from '../types/repository';
import { truncateUuid } from '../utils/formatters';
import './ScoredOrganismsListScreen.css';

interface ScoredOrganismsListScreenProps {
  experimentId: string;
  onScoredOrganismSelect: (scoredOrganismId: string) => void;
  onBack: () => void;
}

const PAGE_SIZE_OPTIONS = [50, 100, 200];

const ScoredOrganismsListScreen: React.FC<ScoredOrganismsListScreenProps> = ({
  experimentId,
  onScoredOrganismSelect,
  onBack,
}) => {
  const [organisms, setOrganisms] = useState<ScoredOrganismSummary[]>([]);
  const [totalCount, setTotalCount] = useState(0);
  const [offset, setOffset] = useState(0);
  const [pageSize, setPageSize] = useState(50);
  const [loading, setLoading] = useState(true);
  const [experimentStatus, setExperimentStatus] = useState<ExperimentStatusData | null>(null);

  const fetchScoredOrganisms = useCallback(async () => {
    try {
      setLoading(true);
      const url = `${API_BASE}/experiment/${experimentId}/scored-organisms?offset=${offset}&limit=${pageSize}`;
      const response = await fetch(url);

      if (!response.ok) {
        throw new Error(`Failed to fetch scored organisms: ${response.statusText}`);
      }

      const data: PaginatedResponse<ScoredOrganismSummary> = await response.json();
      setOrganisms(data.items);
      setTotalCount(data.totalCount);
    } catch (err) {
      console.error('Error fetching scored organisms:', err);
      onBack();
    } finally {
      setLoading(false);
    }
  }, [experimentId, offset, pageSize, onBack]);

  useEffect(() => {
    fetchScoredOrganisms();
  }, [fetchScoredOrganisms]);

  useEffect(() => {
    const fetchStatus = async () => {
      try {
        const response = await fetch(`${API_BASE}/experiment/${experimentId}/status`);
        if (response.ok) {
          const data: ExperimentStatusData = await response.json();
          setExperimentStatus(data);
        }
      } catch (err) {
        console.error('Error fetching experiment status:', err);
      }
    };
    fetchStatus();
  }, [experimentId]);

  const handlePrevious = () => {
    setOffset(Math.max(0, offset - pageSize));
  };

  const handleNext = () => {
    if (offset + pageSize < totalCount) {
      setOffset(offset + pageSize);
    }
  };

  const handlePageSizeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setPageSize(Number(e.target.value));
    setOffset(0);
  };

  const startItem = totalCount === 0 ? 0 : offset + 1;
  const endItem = Math.min(offset + pageSize, totalCount);

  if (loading && organisms.length === 0) {
    return (
      <div className="scored-organisms-list-screen">
        <h1>Scored Organisms</h1>
        <p>Loading...</p>
      </div>
    );
  }

  return (
    <div className="scored-organisms-list-screen">
      <div className="header-row">
        <h1>Scored Organisms</h1>
        <button className="back-button" onClick={onBack}>
          Back to Experiments
        </button>
      </div>

      <div className="experiment-summary">
        <span className="experiment-summary-item">
          <span className="experiment-summary-label">Experiment:</span>
          <span title={experimentId}>{truncateUuid(experimentId)}</span>
        </span>
        {experimentStatus && (
          <>
            <span className="experiment-summary-item">
              <span className="experiment-summary-label">Status:</span>
              <span className={`status-${experimentStatus.status.toLowerCase()}`}>
                {experimentStatus.status}
              </span>
            </span>
            <span className="experiment-summary-item">
              <span className="experiment-summary-label">Cycles:</span>
              <span>{experimentStatus.cyclesCompleted.toLocaleString()}</span>
            </span>
            <span className="experiment-summary-item">
              <span className="experiment-summary-label">Organisms Replaced:</span>
              <span>{experimentStatus.organismsReplaced.toLocaleString()}</span>
            </span>
          </>
        )}
      </div>

      {organisms.length === 0 && !loading ? (
        <p className="no-data-message">No data available</p>
      ) : (
        <>
          <table className="organisms-table">
            <thead>
              <tr>
                <th>Rank</th>
                <th>UUID</th>
                <th>Score</th>
              </tr>
            </thead>
            <tbody>
              {organisms.map((organism, index) => (
                <tr
                  key={organism.id}
                  onClick={() => onScoredOrganismSelect(organism.id)}
                  className="clickable-row"
                >
                  <td>{offset + index + 1}</td>
                  <td title={organism.id}>{truncateUuid(organism.id)}</td>
                  <td>{organism.score.toFixed(6)}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="pagination-controls">
            <div className="page-size-selector">
              <label>
                Items per page:
                <select value={pageSize} onChange={handlePageSizeChange}>
                  {PAGE_SIZE_OPTIONS.map((size) => (
                    <option key={size} value={size}>
                      {size}
                    </option>
                  ))}
                </select>
              </label>
            </div>

            <div className="pagination-info">
              Showing {startItem}-{endItem} of {totalCount}
            </div>

            <div className="pagination-buttons">
              <button onClick={handlePrevious} disabled={offset === 0}>
                Previous
              </button>
              <button onClick={handleNext} disabled={offset + pageSize >= totalCount}>
                Next
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default ScoredOrganismsListScreen;
