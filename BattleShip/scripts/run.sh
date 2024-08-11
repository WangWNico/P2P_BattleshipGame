#!/bin/bash

# Compile the project
echo "Compiling the project..."
./mvnw clean compile

# Run the project
echo "Running the project..."
./mvnw exec:java -Dexec.mainClass="edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship.network.BattleShipApplication"

# Generate Javadocs
echo "Generating Javadocs..."
./mvnw javadoc:javadoc

# Move Javadocs to docs folder
echo "Moving Javadocs to docs folder..."
mkdir -p docs
cp -r target/site/apidocs/* docs/

echo "Done!"
