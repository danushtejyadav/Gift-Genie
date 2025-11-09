#!/bin/sh
# This script starts the Ollama server in the background,
# and then starts the Spring Boot app.

echo "Starting Ollama server in background..."
ollama serve &

# Wait a few seconds for the server to initialize
sleep 5

# --- REMOVED THE 'ollama pull' COMMAND ---
# This is no longer needed because the model is in the Dockerfile

echo "Starting Spring Boot application..."

# These memory-saving flags are now more important than ever
java -XX:MaxRAMPercentage=60.0 -XX:+UseSerialGC -Xss512k \
     -Dspring.main.lazy-initialization=true \
     -jar app.jar