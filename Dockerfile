# ==============================
# Build Stage
# ==============================
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy Maven wrapper + config first
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# 🔑 Make mvnw executable (important for Windows users)
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src

# Build application
RUN ./mvnw clean package -DskipTests

# ==============================
# Run Stage
# ==============================
FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
