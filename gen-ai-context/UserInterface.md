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

When "Experiment" is selected, the following options are made available in the sidebar:
- Start Experiment
- Stop Experiment

#### Start Experiment
The Start Experiment screen presents the current configuration of the experiment.  It allows the user to change the configuration and start the experiment.

#### Stop Experiment
The Stop Experiment screen allows the user to stop the current experiment if one is running.

### Scored Organism Repository

When "Scored Organism Repository" is selected, the following options are made available in the sidebar:
- List Scored Organisms
- Display Top 5 Scored Organisms