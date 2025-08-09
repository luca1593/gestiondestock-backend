FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
FROM openjdk:21-jdk-slim-bullseye
WORKDIR /app
COPY --from=builder /app/target/*.jar gestiondestock.jar
ENTRYPOINT ["java", "-jar", "gestiondestock.jar"]