#!/bin/bash

VM_USER="server"
VM_HOST="172.31.250.176"
REMOTE_DIR="/home/$VM_USER"
JAR_NAME="xmart-zity-backend-1.0-SNAPSHOT-jar-with-dependencies.jar"
LOG_FILE="backend.log"

echo "Start backend on $VM_HOST..."

ssh $VM_USER@$VM_HOST "
  nohup java -jar $REMOTE_DIR/$JAR_NAME > $REMOTE_DIR/$LOG_FILE 2>&1 &
  echo \$! > $REMOTE_DIR/backend.pid
"

echo "Backend started"
