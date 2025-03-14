#!/bin/bash

# Take the script location and generate an absolute path out of it
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Ask the user for the path to the file with the PIDs
echo You are at: $(pwd)
# -e enables autocompletion for more comfort
read -e -p "Please specify the PID file path: " PIDPATHVAR
# Echo the user his input
echo The specified path was: $PIDPATHVAR
# Run the program
java -jar "$SCRIPT_DIR/serverstatuschecker.jar" $PIDPATHVAR --spring.config.location="$SCRIPT_DIR/application.properties"
