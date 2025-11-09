#!/bin/sh
# This script starts the Ollama server in the background,
# pulls the model, and then starts the Spring Boot app.

echo "Starting Ollama server in background..."
ollama serve &

# Wait a few seconds for the server to initialize
sleep 5

# --- THIS COMMAND IS BACK ---
echo "Pulling tinyllama model (this happens on first start)..."
ollama pull tinyllama
# --- END OF COMMAND ---

echo "Starting Spring Boot application..."

# These memory-saving flags are critical
java -XX:MaxRAMPercentage=60.0 -XX:+UseSerialGC -Xss512k \
     -Dspring.main.lazy-initialization=true \
     -jar app.jar