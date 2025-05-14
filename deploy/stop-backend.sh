#!/bin/bash

VM_USER="server"
VM_HOST="172.31.250.176"
PID_FILE="/home/$VM_USER/backend.pid"

echo "Stopping backend on $VM_HOST..."

ssh $VM_USER@$VM_HOST "
  if [ -f $PID_FILE ]; then
    kill -9 \$(cat $PID_FILE) && rm $PID_FILE
    echo 'Backend stopped.'
  else
    echo 'No PID file found. Is the backend running?'
  fi
"
