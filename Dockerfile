# This is my Dockerfile for the JavaSpringboot backend for my Retro Game Market app
# I am going to be approaching this deployment in a multi-stage build to keep the final image size small

# Stage 1: Build the application

# I am using the official Maven image with Eclipse Temurin JDK 17 to build the application. This stage will compile the source code and package it into a JAR file.
FROM maven:3.9-eclipse-temurin-17 AS builder
# Set the working directory in the container
WORKDIR /app
# Copy the pom.xml and source code to the container
COPY pom.xml .
# Download the dependencies to cache them in the layer
RUN mvn dependency:go-offline
# Now copy the source code
COPY src/ ./src/
# Build the application
RUN mvn clean package -DskipTests


# Stage 2: Create the runtime image

# I am using the official Eclipse Temurin Jammy JRE 17 image to run the application.
FROM eclipse-temurin:17-jre-jammy
# Set the working directory in the container
WORKDIR /app

RUN groupadd --system non-root-group \
 && useradd --system --gid non-root-group --home-dir /app --shell /usr/sbin/nologin non-root-user
# Set default environment variables
ENV SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/retro_game_market
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=root1234

# Copy the JAR file from the builder stage
COPY --from=builder --chown=non-root-user:non-root-group /app/target/*.jar app.jar

USER non-root-user

# Expose the port that the application will run on
EXPOSE 8088
# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

