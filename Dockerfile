# --- Stage 1: Build the Spring Boot App ---
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Run the build and skip tests
RUN mvn clean package -DskipTests


# --- Stage 2: Create the Final Production Image ---
FROM eclipse-temurin:21-jdk

# Set environment variables for Ollama
ENV OLLAMA_HOST=0.0.0.0:11434
ENV OLLAMA_ORIGINS=*

# --- Install Ollama ---
# We only need 'curl' now. We are removing 'procps'.
RUN apt-get update && apt-get install -y curl
# Download and install Ollama
RUN curl -L https://ollama.com/download/ollama-linux-amd64 -o /usr/bin/ollama
RUN chmod +x /usr/bin/ollama

# --- THIS IS THE FIX ---
# We start 'ollama serve', get its PID, pull the model, and then 'kill' the PID.
# This avoids 'pkill' and its dependencies.
RUN ollama serve & \
    OLLAMA_PID=$! && \
    sleep 5 && \
    echo "Pulling tinyllama model..." && \
    ollama pull tinyllama && \
    echo "Model pull complete." && \
    kill $OLLAMA_PID
# --- END OF FIX ---

# Set up the application directory
WORKDIR /app

# --- Copy Artifacts ---
COPY --from=build /app/target/gift-recommender-0.0.1-SNAPSHOT.jar app.jar
COPY start.sh .
RUN chmod +x start.sh

# Expose the port
EXPOSE 10000

# The script that will run both Ollama and your app
CMD ["./start.sh"]