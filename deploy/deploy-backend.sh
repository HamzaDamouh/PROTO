#!/bin/bash

set -e

VM_USER="server"
VM_HOST="172.31.250.176"
REMOTE_DIR="/home/$VM_USER"
JAR_NAME="xmart-zity-backend-1.0-SNAPSHOT-jar-with-dependencies.jar"

echo "[1] Build backend JAR..."
cd ../xmart-city-backend
mvn clean compile assembly:single
cd -

echo "[2] SCP JAR --> backend VM..."
scp "../xmart-city-backend/target/$JAR_NAME" $VM_USER@$VM_HOST:$REMOTE_DIR/

echo "[✔] Deployment complete."
