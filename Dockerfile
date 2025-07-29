FROM openjdk:21-jdk-slim-bullseye
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app/gestiondestock.jar
ENTRYPOINT ["java", "-jar", "/app/gestiondestock.jar"]