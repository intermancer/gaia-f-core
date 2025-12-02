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

When "Experiment" is selected the Experiment screen presents the current configuration of the experiment, the current status of the running Experiment, and the command buttons to start or stop the experiment.  If the experiment is not running, the "Start Experiment" button is enabled.  If the experiment is running, the "Stop Experiment" button is enabled.

The Status panel displays the current experiment state and must remain updated throughout the experiment lifecycle. When "Start Experiment" is clicked, the Status panel should poll for status updates to reflect the ongoing experiment progress. Once the experiment completes, the Status panel should automatically update to display "No Experiment Running".

After an Experiment is running, the Status panel will display all of the values in the ExperimentConfiguration and update them every second as the Experiment progresses.  The ExperimentConfiguration values will remain on the Status panel once the Experiment is complete.

### Scored Organism Repository

When "Scored Organism Repository" is selected, the following options are made available in the sidebar:
- List Scored Organisms
- Display Top 5 Scored Organisms