FROM eclipse-temurin:17-jdk-alpine AS build

ARG BUILD_DATE
ARG BUILD_VERSION

WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY mvnw .
COPY .mvn ./.mvn

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

LABEL org.opencontainers.image.created="${BUILD_DATE}"
LABEL org.opencontainers.image.version="${BUILD_VERSION}"
LABEL org.opencontainers.image.source="https://github.com/luca/gestiondestock-backend"

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=build /app/target/gestiondestock.war app.war

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8085

ENV DB_HOST=mysql
ENV DB_PORT=3307
ENV DB_NAME=gestiondestock

ENTRYPOINT ["java", "-jar", "app.war"]
