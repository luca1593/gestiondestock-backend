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

EXPOSE 8080

ENV DB_USERNAME=luca
ENV DB_PASSWORD=luca1593
ENV DB_HOST=mysql
ENV DB_PORT=3306
ENV DB_NAME=gestiondestock
ENV JWT_SECRET_KEY=a3f8b2c7d1e9f4a5b6c8d2e7f3a9b1c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0
ENV FLICKR_API_KEY=your_flickr_api_key
ENV FLICKR_API_SECRET=your_flickr_api_secret
ENV FLICKR_APP_KEY=your_flickr_app_key
ENV FLICKR_APP_SECRET=your_flickr_app_secret
ENV MAIL_HOST=smtp.gmail.com
ENV MAIL_PORT=587
ENV MAIL_USERNAME=gestion-stock@dev-tech.com
ENV MAIL_PASSWORD=your_mail_password

ENTRYPOINT ["java", "-jar", "app.war"]
