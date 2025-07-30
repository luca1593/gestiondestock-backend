FROM openjdk:21-jdk-slim-bullseye
ARG MAVEN_VERSION=3.9.5
ARG MAVEN_HOME=/usr/share/maven
ARG MAVEN_CONFIG=/root/.m2
ENV MAVEN_HOME=${MAVEN_HOME} \
    MAVEN_CONFIG=${MAVEN_CONFIG}
ENV PATH=${MAVEN_HOME}/bin:${PATH}
RUN apt-get update && \
    apt-get install -y maven && \
    [ -L /usr/bin/mvn ] || [ ! -e /usr/bin/mvn ] && ln -s ${MAVEN_HOME}/bin/mvn /usr/bin/mvn || true && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app/gestiondestock.jar
ENTRYPOINT ["java", "-jar", "/app/gestiondestock.jar"]