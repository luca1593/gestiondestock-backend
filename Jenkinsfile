pipeline {
    agent any

    environment {
        APP_NAME = 'gestiondestock-backend'
        IMAGE_NAME = 'gestiondestock-backend'
        IMAGE_TAG = "${BUILD_NUMBER}"
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
                docker compose down || true
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
        }

        stage('Build Docker Image') {
            steps {
                sh '''
                docker build --no-cache -t gestiondestock-backend:latest .
                '''
            }
        }

        stage('Deploy Application') {
            steps {
                sh '''
                docker compose up -d --build
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                docker compose ps
                docker logs gestiondestock-backend --tail 50 || true
                '''
            }
        }
    }

    post {

        success {
            echo "Application deployed successfully"
        }

        failure {
            echo "Deployment failed"

            sh '''
            docker compose logs backend || true
            docker compose logs mysql || true
            '''
        }

        always {
            cleanWs()
        }
    }
}