#!/usr/bin/env bash

VM_USER="server"
VM_HOST="172.31.250.176"
PORT=45065

echo "Stopping backend on $VM_HOST…"

# -T disables pseudo-tty allocation so we don't get the motd banner
ssh -T $VM_USER@$VM_HOST <<EOF
  # look up the PID listening on our TCP port
  PID=\$(lsof -ti tcp:$PORT)
  if [ -n "\$PID" ]; then
    echo "Killing backend process PID=\$PID"
    kill -9 \$PID
    echo "Backend stopped."
  else
    echo "No process found listening on port $PORT."
  fi
EOF
