import React from 'react';
import './Sidebar.css';

interface SidebarProps {
  selectedCommand: string | null;
  onCommandSelect: (command: string) => void;
}

const Sidebar: React.FC<SidebarProps> = ({ selectedCommand, onCommandSelect }) => {
  const mainCommands = ['Experiment', 'Repository'];

  const getSecondaryCommands = (): string[] => {
    // Repository no longer has secondary commands - navigation is handled
    // through clickable lists in the main content area
    return [];
  };

  // Determine which main command is currently active
  const getActiveMainCommand = (selected: string | null): string | null => {
    if (!selected) return null;
    
    // Check if selected is a main command
    if (mainCommands.includes(selected)) {
      return selected;
    }
    
    // Check if selected is a secondary command and return its parent
    for (const mainCommand of mainCommands) {
      if (getSecondaryCommands().includes(selected)) {
        return mainCommand;
      }
    }
    
    return null;
  };

  const activeMainCommand = getActiveMainCommand(selectedCommand);

  return (
    <div className="sidebar">
      <h2>GAIA-F</h2>
      <ul className="command-list">
        {mainCommands.map((command) => (
          <li key={command} className="main-command">
            <button
              className={`command-button ${activeMainCommand === command ? 'active' : ''}`}
              onClick={() => onCommandSelect(command)}
            >
              {command}
            </button>
            {activeMainCommand === command && getSecondaryCommands().length > 0 && (
              <ul className="secondary-commands">
                {getSecondaryCommands().map((subCommand) => (
                  <li key={subCommand} className="secondary-command">
                    <button
                      className="secondary-button"
                      onClick={() => onCommandSelect(subCommand)}
                    >
                      {subCommand}
                    </button>
                  </li>
                ))}
              </ul>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Sidebar;