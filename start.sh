#!/bin/sh
# This script starts the Ollama server in the background,
# pulls the model, and then starts the Spring Boot app.

echo "Starting Ollama server in background..."
ollama serve &

# Wait a few seconds for the server to initialize
sleep 5

echo "Pulling llama3:8b model..."
# This will only pull if the model isn't already present
ollama pull llama3:8b

echo "Starting Spring Boot application..."
# This will run in the foreground, keeping the container alive
java -jar app.jar