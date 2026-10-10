# Multi-stage Docker build for PoojaMart Spring Boot
# Stage 1: Build JAR using Maven
FROM maven:3.8.8-eclipse-temurin-11 AS builder
WORKDIR /build

COPY pom.xml .
# Pre-fetch dependencies to speed up subsequent builds
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime container using lightweight JRE
FROM eclipse-temurin:11-jre-alpine
WORKDIR /app

# Copy the built jar from builder
COPY --from=builder /build/target/*.jar app.jar

# Render injects the PORT environment variable
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=render
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
