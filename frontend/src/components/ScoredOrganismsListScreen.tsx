import React, { useEffect, useState, useCallback } from 'react';
import type { ScoredOrganismSummary, PaginatedResponse } from '../types/repository';
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

  const fetchScoredOrganisms = useCallback(async () => {
    try {
      setLoading(true);
      const url = `http://localhost:8080/gaia-f/experiment/${experimentId}/scored-organisms?offset=${offset}&limit=${pageSize}`;
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

      <p className="experiment-info">Experiment: {truncateUuid(experimentId)}</p>

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
