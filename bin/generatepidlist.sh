#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Generate pidlist.txt dynamically with the detached screen sessions
echo "Updating PID list..."
screen -ls | awk '/Detached/ {split($1, a, "."); print a[2]; print a[1]}' > "$SCRIPT_DIR/pidlist.txt"

# Show user the generated PIDs
echo "Generated PID list (Detached Screens Only):"
cat "$SCRIPT_DIR/pidlist.txt"