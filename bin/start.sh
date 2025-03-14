#!/bin/bash
echo Please specify the PID file path:
read serverstatuscheckerpathvar
echo The specified path was: $serverstatuscheckerpathvar
java -jar ./serverstatuschecker.jar $serverstatuscheckerpathvar --spring.config.location=./application.properties
