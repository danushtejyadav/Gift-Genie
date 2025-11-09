# --- Stage 1: Build the Spring Boot App ---
# We name this stage 'build'. It will be discarded later.
FROM maven:3.8.5-openjdk-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Run the build and skip tests
RUN mvn clean package -DskipTests


# --- Stage 2: Create the Final Production Image ---
# We start fresh from the Java runtime image
FROM eclipse-temurin:21-jdk

# Set environment variables for Ollama
ENV OLLAMA_HOST=0.0.0.0:11434
ENV OLLAMA_ORIGINS=*

# --- Install Ollama ---
# We need curl to download the installer
RUN apt-get update && apt-get install -y curl
# Download and install Ollama
RUN curl -L https://ollama.com/download/ollama-linux-amd64 -o /usr/bin/ollama
RUN chmod +x /usr/bin/ollama

# Set up the application directory
WORKDIR /app

# --- Copy Artifacts ---
# 1. Copy the .jar file from the 'build' stage (Stage 1)
COPY --from=build /app/target/gift-recommender-0.0.1-SNAPSHOT.jar app.jar
# 2. Copy the startup script from your local machine
COPY start.sh .
RUN chmod +x start.sh

# Expose the port your Spring app runs on
# This must match the 10000 in your application.properties
EXPOSE 10000

# The script that will run both Ollama and your app
CMD ["./start.sh"]