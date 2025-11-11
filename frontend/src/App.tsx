import { useState } from 'react';
import Sidebar from './components/Sidebar';
import MainContent from './components/MainContent';
import './App.css';

function App() {
  const [selectedCommand, setSelectedCommand] = useState<string | null>(null);

  const handleCommandSelect = (command: string) => {
    setSelectedCommand(command);
  };

  return (
    <div className="app">
      <Sidebar 
        selectedCommand={selectedCommand} 
        onCommandSelect={handleCommandSelect} 
      />
      <MainContent selectedCommand={selectedCommand} />
    </div>
  );
}

export default App;