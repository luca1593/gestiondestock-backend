pipeline {
    agent any

    environment {
        APP_NAME = 'gestiondestock-backend'
        IMAGE_NAME = 'gestiondestock-backend'
        IMAGE_TAG = "${BUILD_NUMBER}"
        // Variables pour Docker BuildKit et DNS
        DOCKER_BUILDKIT = '0'  // Désactiver BuildKit pour éviter les problèmes DNS
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                echo "Source code downloaded"
            }
        }

        stage('Verify Docker') {
            steps {
                sh '''
                docker --version
                docker compose version
                '''
            }
        }

        stage('Create ENV File') {
            steps {
                echo "Creating .env file"

                sh '''
cat <<EOF > .env
SPRING_PROFILES_ACTIVE=prod

MYSQL_ROOT_PASSWORD=rootpassword
DB_NAME=gestiondestock
DB_USERNAME=luca
DB_PASSWORD=luca1593

JWT_SECRET_KEY=+ORJQdAuRJgWSiRMu+3Sq401f0pUMajBZwVRYwno5fiiTp4vxJ42Aiou2tUQipxLknqtEVFStKVB/m9TjJxdPg==

FLICKR_API_KEY=abc11f1e268d908eeee176297269c001
FLICKR_API_SECRET=50a8ae15b4224302
FLICKR_APP_KEY=72157720855398857-f67e55f056acdd0d
FLICKR_APP_SECRET=2ba345379070ffc8

MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=mpiasaorange@gmail.com
MAIL_PASSWORD=JustMe12

SWAGGER_ENABLED=false
SHOW_HEALTH_DETAILS=false

BUILD_DATE=latest
BUILD_VERSION=latest

WATCHTOWER_NOTIFICATION_URL=
EOF
                '''
            }
        }

        stage('Stop Old Containers') {
            steps {
                sh '''
                docker compose -f docker-compose.prod.yml down  --remove-orphans || true
                '''
            }
        }

        stage('Build Spring Boot') {
            steps {
                sh '''
                chmod +x mvnw
                ./mvnw clean package -DskipTests
                '''
            }
        }

        stage('Run Tests') {
            steps {
                sh './mvnw test'
            }
            post {
               always {
                   junit '**/target/surefire-reports/*.xml'  // Publier les résultats des tests
               }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh '''
                docker build --no-cache --network=host -t ${IMAGE_NAME}:latest .
                docker tag ${IMAGE_NAME}:latest ${IMAGE_NAME}:${IMAGE_TAG}
                '''
            }
        }

        stage('Deploy Application') {
            steps {
                sh '''
                docker compose -f docker-compose.prod.yml down --remove-orphans || true
                docker compose -f docker-compose.prod.yml up -d --build --force-recreate
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                script {
                    sh '''
                    echo "Checking container status..."
                    docker compose -f docker-compose.prod.yml ps
                    echo "Getting backend logs..."
                     docker compose -f docker-compose.prod.yml logs backend --tail 50 || true
                     echo "Waiting for application to start..."
                     sleep 30
                     echo "Checking application health..."
                     for i in 1 2 3 4 5 6 7 8 9 10; do
                        if curl -s -f http://localhost:8080/actuator/health; then
                            cho "✅ Application is healthy and accessible"
                            exit 0
                        fi
                        echo "⏳ Waiting for application... attempt $i/10"
                        sleep 10
                     done
                     echo "❌ Application health check failed after 100 seconds"
                     docker compose -f docker-compose.prod.yml logs backend --tail 200
                     exit 1
                     '''
                }
            }
        }
    }

    post {

        success {
            echo "Application deployed successfully"
            sh '''
            echo "Deployment Summary:"
            echo "- Image: ${IMAGE_NAME}:${IMAGE_TAG}"
            echo "- Containers running:"
            docker compose -f docker-compose.prod.yml ps
            '''
        }

        failure {
            echo "❌ Deployment failed - collecting diagnostics..."

            sh '''
            echo "=== Docker Compose Logs ==="
            docker compose -f docker-compose.prod.yml logs backend --tail 200 || true
            docker compose -f docker-compose.prod.yml logs mysql --tail 100 || true
            docker compose -f docker-compose.prod.yml logs nginx --tail 100 || true
            echo "=== Container Status ==="
            docker compose -f docker-compose.prod.yml ps || true
            echo "=== Docker Images ==="
            docker images | grep ${IMAGE_NAME} || true
            echo "=== Network Check ==="
            docker network ls || true
            '''
        }

         always {
            script {
                // Nettoyage des anciennes images (optionnel)
                sh '''
                # Garder seulement les 2 dernières images
                docker images ${IMAGE_NAME} --format "table {{.Tag}}" | tail -n +2 | head -n -2 | xargs -r docker rmi || true
                '''
            }
         }
    }
}