#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo Please specify the PID file path:
read serverstatuscheckerpathvar
echo The specified path was: $serverstatuscheckerpathvar
java -jar "$SCRIPT_DIR/serverstatuschecker.jar" $serverstatuscheckerpathvar --spring.config.location="$SCRIPT_DIR/application.properties"
