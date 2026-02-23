# Stage 1: Build frontend
FROM node:20-alpine AS frontend
WORKDIR /app
COPY attendance-frontend/package*.json ./
RUN npm ci
COPY attendance-frontend/ ./
RUN npm run build

# Stage 2: Build backend JAR (with embedded frontend)
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline -B -q
COPY src ./src
COPY --from=frontend /app/dist ./src/main/resources/static
RUN mvn package -DskipTests -B -q

# Stage 3: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/attendanceApp-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
