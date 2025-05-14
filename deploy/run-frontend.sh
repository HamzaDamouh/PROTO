#!/bin/bash

echo "Launching JavaFX frontend..."

cd ../xmart-frontend || exit 1

mvn clean compile exec:java -Dexec.mainClass="edu.ezip.ing1.pds.JavaFxLauncher"
