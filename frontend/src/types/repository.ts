/**
 * Type definitions for Repository UI data structures.
 */

export type ExperimentState = 'STOPPED' | 'RUNNING' | 'PAUSED' | 'EXCEPTION';

export interface ExperimentSummary {
  id: string;
  createdAt: string; // ISO 8601 format from Java Instant
  status: ExperimentState;
}

export interface ScoredOrganismSummary {
  id: string;
  score: number;
}

export interface ScoredOrganism {
  id: string;
  score: number;
  organismId: string;
  organism: object;
  experimentId: string;
}

export interface PaginatedResponse<T> {
  items: T[];
  totalCount: number;
  offset: number;
  limit: number;
}
