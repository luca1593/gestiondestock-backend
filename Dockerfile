FROM eclipse-temurin:17-jdk-alpine AS build

# Configuration DNS Alpine
RUN echo "nameserver 8.8.8.8" > /etc/resolv.conf && \
    echo "nameserver 1.1.1.1" >> /etc/resolv.conf && \
    apk update --no-cache && \
    apk add --no-cache wget ca-certificates curl && \
    update-ca-certificates

ARG BUILD_DATE
ARG BUILD_VERSION

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -U -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=build /app/target/gestiondestock.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
