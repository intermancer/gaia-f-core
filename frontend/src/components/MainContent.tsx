import React, { useState, useEffect } from 'react';
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

  // Reset repository navigation when switching away from Repository
  useEffect(() => {
    if (selectedCommand !== 'Repository') {
      setRepositoryView('experiments');
      setSelectedRepoExperimentId(null);
      setSelectedScoredOrganismId(null);
    }
  }, [selectedCommand]);

  const handleStartExperiment = async () => {
    try {
      setExperimentStatus('Starting experiment...');
      setIsRunning(true);

      const response = await fetch('http://localhost:8080/gaia-f/experiment/start', {
        method: 'POST',
      });

      if (!response.ok) {
        // noinspection ExceptionCaughtLocallyJS
        throw new Error(`Failed to start experiment: ${response.statusText}`);
      }

      const expId = await response.text();
      setExperimentId(expId);
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
  };

  const handleBackToScoredOrganisms = () => {
    setRepositoryView('scoredOrganisms');
    setSelectedScoredOrganismId(null);
  };

  const renderRepositoryContent = () => {
    switch (repositoryView) {
      case 'experiments':
        return <ExperimentsListScreen onExperimentSelect={handleExperimentSelect} />;

      case 'scoredOrganisms':
        if (!selectedRepoExperimentId) {
          return <ExperimentsListScreen onExperimentSelect={handleExperimentSelect} />;
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
        return <ExperimentsListScreen onExperimentSelect={handleExperimentSelect} />;
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
