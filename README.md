#Data Service

You can use dev Bash script for deploying and managing a Java application on the local system or remote server. It includes features for building the project, deploying it to the server, starting and stopping the service, and viewing logs. The script is designed to be extensible and easy to use.

## Features

- Dependency check: Ensures that required dependencies (`pv` and `xmlstarlet`) are installed.
- Modular structure: Organizes tasks into functions for improved readability and maintenance.
- Logging: Logs script output to a `deployment.log` file for tracking and debugging.
- Error handling: Gracefully handles errors and logs error messages.
- Service management: Allows starting, stopping, and restarting the remote service.
- SSH key management: Supports SSH key generation and copying to the remote host.
- Configuration: Easily configurable variables for customization.

## Usage

1. Clone or download the project from mapna repository on your local machine.
2. Make the script executable: `chmod +x dev`
3. Run the script with one of the following commands:

    - `./dev build`: Check dependencies, and build the project.
    - `./dev run`: Check dependencies, build the project, and run the service on local system.
    - `./dev deploy`: Check dependencies, generate SSH key if not exist, build, and deploy.
    - `./dev all`: Check dependencies, generate SSH key if not exist, build, deploy, restart service, and show service logs.
    - `./dev start`: Check dependencies, generate SSH key if not exist, and start the remote service.
    - `./dev stop`: Check dependencies, generate SSH key if not exist, and stop the remote service.
    - `./dev restart`: Check dependencies, generate SSH key if not exist, and restart the remote service.
    - `./dev log`: Check dependencies, generate SSH key if not exist, and display logs from the remote host.

## Logging

The script logs its output to a `deployment.log` file in the same directory as the script. You can check this log file for information about the script's execution and any error messages encountered.
