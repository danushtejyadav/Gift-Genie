# We use a base image with Java 21 (or 17)
# We'll install Ollama on top of it manually
FROM eclipse-temurin:21-jdk

# Set environment variables
ENV OLLAMA_HOST=0.0.0.0:11434
ENV OLLAMA_ORIGINS=*

# --- Install Ollama ---
# We'll download the Ollama installer script and run it
RUN apt-get update && apt-get install -y curl
RUN curl -L https://ollama.com/download/ollama-linux-amd64 -o /usr/bin/ollama
RUN chmod +x /usr/bin/ollama

# --- Build the Spring Boot App (in a separate stage) ---
FROM maven:3.8.5-openjdk-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# --- Create the Final Image ---
WORKDIR /app

# Copy the .jar file from the 'build' stage
COPY --from=build /app/target/gift-recommender-0.0.1-SNAPSHOT.jar app.jar

# Copy the startup script (we will create this next)
COPY start.sh .
RUN chmod +x start.sh

# Expose the Spring Boot port
EXPOSE 8080

# This script will run when the container starts
CMD ["./start.sh"]