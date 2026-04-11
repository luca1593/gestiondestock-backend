cd "/var/lib/jenkins/workspace/Gestion de stock"

# Sauvegarder l'ancien Dockerfile
mv Dockerfile Dockerfile.maven 2>/dev/null || true

# Créer le nouveau Dockerfile
cat > Dockerfile << 'EOF'
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app
COPY target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]