FROM openjdk:21-jdk-slim-bullseye
WORKDIR /app
RUN echo "Chemin de travail : ${PWD}" && \
    echo "Contenu du répertoire de travail :" && \
    ls -lR
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app/gestiondestock.jar
ENTRYPOINT ["java", "-jar", "/app/gestiondestock.jar"]