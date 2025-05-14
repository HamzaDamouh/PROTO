#!/bin/bash

VM_USER="server"
VM_HOST="172.31.250.176"
PORT=45065

echo "Checking backend status on $VM_HOST..."

ssh $VM_USER@$VM_HOST "
  if lsof -i tcp:$PORT | grep LISTEN; then
    echo 'Backend is running on port $PORT.'
  else
    echo 'Backend not running'
  fi
"
