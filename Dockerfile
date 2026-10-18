# Stage 1: Build avec Maven
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copier les fichiers de configuration Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Copier le code source
COPY src src

# Compiler l'application
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

# Rien a installer dans cette image : le healthcheck utilise `wget`, fourni par
# busybox dans Alpine (les healthchecks des docker-compose font deja de meme).
# `curl` n'est utilise nulle part, ni dans l'image ni par les services.
#
# Ne JAMAIS ecrire dans /etc/resolv.conf : certains builders le montent en
# lecture seule (Render / BuildKit) et le build echoue immediatement avec
# "/bin/sh: can't create /etc/resolv.conf: Read-only file system".
# La resolution DNS est du ressort du builder, pas du Dockerfile.

# Variables de build
ARG BUILD_DATE
ARG BUILD_VERSION

LABEL org.opencontainers.image.created="${BUILD_DATE}"
LABEL org.opencontainers.image.version="${BUILD_VERSION}"

WORKDIR /app

# Copier le JAR depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Port d'ecoute par defaut (local, docker-compose, Jenkins : SERVER_PORT=8080/8085).
# A l'execution, l'application suit l'ordre ${PORT} -> ${SERVER_PORT} -> 8085 :
# Render injecte PORT (defaut 10000) et scanne ce port pour router le trafic.
EXPOSE 8080

# Healthcheck : `wget` de busybox (present dans l'image de base), sur le port reel
# d'ecoute.
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- "http://127.0.0.1:${PORT:-8080}/actuator/health" || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]