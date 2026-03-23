import React, { useState, useEffect } from 'react';
import { API_BASE } from '../utils/api';
import './MainContent.css';
import WelcomeScreen from './WelcomeScreen';
import ExperimentStatusView from './ExperimentStatusView';
import ExperimentsListScreen from './ExperimentsListScreen';
import ScoredOrganismsListScreen from './ScoredOrganismsListScreen';
import ScoredOrganismDetailScreen from './ScoredOrganismDetailScreen';

interface MainContentProps {
  selectedCommand: string | null;
}

type RepositoryView = 'experiments' | 'scoredOrganisms' | 'detail';

const MainContent: React.FC<MainContentProps> = ({ selectedCommand }) => {
  const [experimentStatus, setExperimentStatus] = useState<string>('No experiment running');
  const [isRunning, setIsRunning] = useState<boolean>(false);
  const [experimentId, setExperimentId] = useState<string | null>(null);

  // Repository navigation state
  const [repositoryView, setRepositoryView] = useState<RepositoryView>('experiments');
  const [selectedRepoExperimentId, setSelectedRepoExperimentId] = useState<string | null>(null);
  const [selectedScoredOrganismId, setSelectedScoredOrganismId] = useState<string | null>(null);
  const [experimentsRefreshKey, setExperimentsRefreshKey] = useState<number>(0);

  // Reset repository navigation when switching away from Repository
  useEffect(() => {
    if (selectedCommand !== 'Repository') {
      setRepositoryView('experiments');
      setSelectedRepoExperimentId(null);
      setSelectedScoredOrganismId(null);
    } else {
      // Navigated to Repository — refresh the experiments list
      setExperimentsRefreshKey(k => k + 1);
    }
  }, [selectedCommand]);

  const handleStartExperiment = async () => {
    try {
      setExperimentStatus('Starting experiment...');

      const response = await fetch(`${API_BASE}/experiment/start`, {
        method: 'POST',
      });

      if (!response.ok) {
        // noinspection ExceptionCaughtLocallyJS
        throw new Error(`Failed to start experiment: ${response.statusText}`);
      }

      const expId = await response.text();
      // Set experimentId before isRunning so the polling interval starts
      // with the correct experiment ID already in scope.
      setExperimentId(expId);
      setIsRunning(true);
      setExperimentStatus('Experiment running');
    } catch (error) {
      setExperimentStatus(`Error: ${error instanceof Error ? error.message : 'Unknown error'}`);
      setIsRunning(false);
    }
  };

  const handleStatusChange = (newIsRunning: boolean, newStatus: string) => {
    setIsRunning(newIsRunning);
    setExperimentStatus(newStatus);
  };

  // Repository navigation handlers
  const handleExperimentSelect = (expId: string) => {
    setSelectedRepoExperimentId(expId);
    setRepositoryView('scoredOrganisms');
  };

  const handleScoredOrganismSelect = (scoredOrgId: string) => {
    setSelectedScoredOrganismId(scoredOrgId);
    setRepositoryView('detail');
  };

  const handleBackToExperiments = () => {
    setRepositoryView('experiments');
    setSelectedRepoExperimentId(null);
    setSelectedScoredOrganismId(null);
    setExperimentsRefreshKey(k => k + 1);
  };

  const handleBackToScoredOrganisms = () => {
    setRepositoryView('scoredOrganisms');
    setSelectedScoredOrganismId(null);
  };

  const renderRepositoryContent = () => {
    switch (repositoryView) {
      case 'experiments':
        return <ExperimentsListScreen onExperimentSelect={handleExperimentSelect} refreshKey={experimentsRefreshKey} />;

      case 'scoredOrganisms':
        if (!selectedRepoExperimentId) {
          return <ExperimentsListScreen onExperimentSelect={handleExperimentSelect} refreshKey={experimentsRefreshKey} />;
        }
        return (
          <ScoredOrganismsListScreen
            experimentId={selectedRepoExperimentId}
            onScoredOrganismSelect={handleScoredOrganismSelect}
            onBack={handleBackToExperiments}
          />
        );

      case 'detail':
        if (!selectedScoredOrganismId) {
          return <ExperimentsListScreen onExperimentSelect={handleExperimentSelect} />;
        }
        return (
          <ScoredOrganismDetailScreen
            scoredOrganismId={selectedScoredOrganismId}
            onBack={handleBackToScoredOrganisms}
            onBackToExperiments={handleBackToExperiments}
          />
        );

      default:
        return <ExperimentsListScreen onExperimentSelect={handleExperimentSelect} refreshKey={experimentsRefreshKey} />;
    }
  };

  const renderContent = () => {
    if (!selectedCommand) {
      return <WelcomeScreen />;
    }

    switch (selectedCommand) {
      case 'Experiment':
        return (
          <ExperimentStatusView
            experimentStatus={experimentStatus}
            isRunning={isRunning}
            experimentId={experimentId}
            onStartExperiment={handleStartExperiment}
            onStatusChange={handleStatusChange}
          />
        );

      case 'Repository':
        return renderRepositoryContent();

      default:
        return (
          <div className="default-view">
            <h1>{selectedCommand}</h1>
            <p>Content for {selectedCommand} will be displayed here.</p>
          </div>
        );
    }
  };

  return <div className="main-content">{renderContent()}</div>;
};

export default MainContent;
