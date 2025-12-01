import React from 'react';
import './Sidebar.css';

interface SidebarProps {
  selectedCommand: string | null;
  onCommandSelect: (command: string) => void;
}

const Sidebar: React.FC<SidebarProps> = ({ selectedCommand, onCommandSelect }) => {
  const mainCommands = ['Experiment', 'Scored Organism Repository'];

  const getSecondaryCommands = (command: string): string[] => {
    switch (command) {
      case 'Scored Organism Repository':
        return ['List Scored Organisms', 'Display Top 5 Scored Organisms'];
      default:
        return [];
    }
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
      if (getSecondaryCommands(mainCommand).includes(selected)) {
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
            {activeMainCommand === command && getSecondaryCommands(command).length > 0 && (
              <ul className="secondary-commands">
                {getSecondaryCommands(command).map((subCommand) => (
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