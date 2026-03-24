# This is my Dockerfile for the JavaSpringboot backend for my Retro Game Market app

# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the target directory to the container
COPY target/retro-game-market-0.0.1-SNAPSHOT.jar /app/retro-game-market-0.0.1-SNAPSHOT.jar

# Expose the port that the application will run on
EXPOSE 8088

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "retro-game-market-0.0.1-SNAPSHOT.jar"]


