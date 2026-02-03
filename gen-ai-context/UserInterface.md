# User Interface

## General Layout

The UI is simple, with a sidebar and a main content area.

The title, shown at the top of the sidebar, is "GAIA-F".

## Sidebar

The sidebar contains a list of all the available commands.  Main commands are listed first and always shown.  If a main command has secondary commands or a submenu, it will show up when the main command is selected.  The commands that are not selected should be collapsed.

## Main Content

The main content area is where status and user input is displayed. Main Content screens are implemented in their own components.

## Commands

### Experiment

When "Experiment" is selected the Experiment screen presents the current configuration of the experiment, the current status of the running Experiment, and the command buttons to control experiment execution.

#### Configuration Panel

The Configuration panel displays editable fields for experiment parameters:
- **Cycle Count**: The number of experiment cycles to run (numeric input)
- **Repository Capacity**: The maximum number of organisms in the repository (numeric input)
- **Pausable**: A checkbox indicating whether the experiment can be paused
- **Pause Cycles**: A numeric input field that appears when Pausable is checked, specifying after how many cycles the experiment will automatically pause (0 means no auto-pause, manual pause only)

Configuration fields are disabled while an experiment is running or paused. Users must save configuration changes before starting a new experiment.

#### Status Panel

The Status panel displays the current experiment state and must remain updated throughout the experiment lifecycle. When "Start Experiment" is clicked, the Status panel should poll for status updates to reflect the ongoing experiment progress. 

The status badge displays one of the following states:
- **No Experiment Running**: Initial state before any experiment starts
- **Experiment Running**: Experiment is actively executing cycles
- **Experiment Paused**: Experiment is paused and can be resumed
- **Experiment Error**: Experiment encountered an exception

After an Experiment starts running, the Status panel will display all of the values in the ExperimentConfiguration and update them every second as the Experiment progresses. Progress metrics (cycles completed, organisms replaced) are displayed and updated in real-time. The ExperimentConfiguration values and final progress metrics remain on the Status panel once the Experiment completes.

#### Control Buttons

The button section contains experiment control buttons with the following behavior:

**Start Experiment Button**
- Enabled when: No experiment is running and configuration has no unsaved changes
- Disabled when: An experiment is running, paused, or there are unsaved configuration changes
- Action: Starts a new experiment with the current configuration

**Pause Experiment Button**
- Visible when: An experiment is running and pausable is true
- Enabled when: Experiment status is RUNNING
- Action: Pauses the running experiment, changing button to "Resume Experiment"

**Resume Experiment Button**
- Replaces "Pause Experiment" button when experiment is paused
- Enabled when: Experiment status is PAUSED
- Action: Resumes the paused experiment from where it left off

**Stop Experiment Button**
- Enabled when: An experiment is running or paused
- Disabled when: No experiment is active
- Action: Stops the experiment (future implementation)

### Scored Organism Repository

When "Scored Organism Repository" is selected, the following options are made available in the sidebar:
- List Scored Organisms
- Display Top 5 Scored Organisms