import React, { useState } from 'react';
import './MainContent.css';
import WelcomeScreen from './WelcomeScreen';
import ExperimentStatusView from './ExperimentStatusView';
import ScoredOrganismRepositoryView from './ScoredOrganismRepositoryView';
import ListScoredOrganismsScreen from './ListScoredOrganismsScreen';
import DisplayTop5ScoredOrganismsScreen from './DisplayTop5ScoredOrganismsScreen';

interface MainContentProps {
  selectedCommand: string | null;
}

const MainContent: React.FC<MainContentProps> = ({ selectedCommand }) => {
  const [experimentStatus, setExperimentStatus] = useState<string>('No experiment running');
  const [isRunning, setIsRunning] = useState<boolean>(false);
  const [experimentId, setExperimentId] = useState<string | null>(null);

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
      
      case 'Scored Organism Repository':
        return <ScoredOrganismRepositoryView />;
      
      case 'List Scored Organisms':
        return <ListScoredOrganismsScreen />;
      
      case 'Display Top 5 Scored Organisms':
        return <DisplayTop5ScoredOrganismsScreen />;
      
      default:
        return (
          <div className="default-view">
            <h1>{selectedCommand}</h1>
            <p>Content for {selectedCommand} will be displayed here.</p>
          </div>
        );
    }
  };

  return (
    <div className="main-content">
      {renderContent()}
    </div>
  );
};

export default MainContent;