# Stage 1: Build the JAR using Maven Wrapper
FROM eclipse-temurin:17 AS builder
WORKDIR /app
# Copy Maven build files and code
COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src ./src
# Build the executable JAR
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the final Image
FROM eclipse-temurin:17-jre-focal
WORKDIR /app
# Get JAR from builder stage
ARG JAR_FILE=target/*.jar
COPY --from=builder /app/${JAR_FILE} app.jar

# Expose the application port
EXPOSE 8080

# The command to run the application
ENTRYPOINT [ "java", "-jar", "app.jar" ]
