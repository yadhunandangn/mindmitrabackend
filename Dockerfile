# ============================
# Stage 1 — Build the app
# ============================
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Copy Maven config first (better caching)
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline

# Copy the source and build
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ============================
# Stage 2 — Run the app
# ============================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the jar from the build stage
ARG JAR_FILE=/workspace/target/*.jar
COPY --from=builder ${JAR_FILE} app.jar

# Tell Spring Boot to bind to Render's dynamic PORT
ENV SERVER_PORT=${PORT:-8080}
EXPOSE 8080

# Start the app
ENTRYPOINT ["java","-jar","/app.jar"]
