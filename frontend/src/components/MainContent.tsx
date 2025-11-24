import React, { useState, useEffect } from 'react';
import './MainContent.css';
import WelcomeScreen from './WelcomeScreen';
import ExperimentStatusView from './ExperimentStatusView';
import StartExperimentScreen from './StartExperimentScreen';
import StopExperimentScreen from './StopExperimentScreen';
import ScoredOrganismRepositoryView from './ScoredOrganismRepositoryView';
import ListScoredOrganismsScreen from './ListScoredOrganismsScreen';
import DisplayTop5ScoredOrganismsScreen from './DisplayTop5ScoredOrganismsScreen';

interface MainContentProps {
  selectedCommand: string | null;
}

const MainContent: React.FC<MainContentProps> = ({ selectedCommand }) => {
  const [experimentStatus, setExperimentStatus] = useState<string>('No experiment running');
  const [isRunning, setIsRunning] = useState<boolean>(false);

  // Use useEffect to handle side effects when commands are clicked
  useEffect(() => {
    if (selectedCommand === 'Stop Experiment') {
      handleStopExperiment();
    }
  }, [selectedCommand]);

  const handleStopExperiment = async () => {
    try {
      // TODO: Replace with actual API call to /gaia-f/experiment/stop when endpoint exists
      setExperimentStatus('Stopping experiment...');
      
      // Simulated API call
      setTimeout(() => {
        setExperimentStatus('Experiment stopped');
        setIsRunning(false);
      }, 1000);
    } catch (error) {
      setExperimentStatus(`Error: ${error instanceof Error ? error.message : 'Unknown error'}`);
    }
  };

  const renderContent = () => {
    if (!selectedCommand) {
      return <WelcomeScreen />;
    }

    switch (selectedCommand) {
      case 'Experiment':
        return <ExperimentStatusView experimentStatus={experimentStatus} isRunning={isRunning} />;
      
      case 'Start Experiment':
        return (
          <StartExperimentScreen 
            experimentStatus={experimentStatus}
            isRunning={isRunning}
            onStatusChange={setExperimentStatus}
            onRunningChange={setIsRunning}
          />
        );
      
      case 'Stop Experiment':
        return <StopExperimentScreen experimentStatus={experimentStatus} isRunning={isRunning} />;
      
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