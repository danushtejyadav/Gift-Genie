# Stage 1: Build the application with Maven
# We use a specific Java/Maven image to build the .jar file
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final, lightweight runtime image
# We use a slim Java-only image to run the app
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the .jar file from the 'build' stage
COPY --from=build /app/target/gift-recommender-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 (which Spring Boot uses by default)
EXPOSE 8080

# The command to run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]