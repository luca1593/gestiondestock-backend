pipeline {
    agent any

    environment {
        APP_NAME = 'gestiondestock-backend'
        IMAGE_NAME = 'gestiondestock-backend'
        IMAGE_TAG = "${BUILD_NUMBER}"
        COMPOSE_PROJECT_NAME = 'gestiondestock'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                echo "Source code checked out successfully"
            }
        }

        stage('Verify Docker') {
            steps {
                echo "Checking Docker installation..."
                sh '''
                    docker --version
                    docker compose version
                '''
            }
        }

        stage('Clean Old Containers') {
            steps {
                echo "Stopping old containers..."
                sh '''
                    docker compose down || true
                '''
            }
        }

        stage('Clean Old Images') {
            steps {
                echo "Cleaning old Docker images..."
                sh '''
                    docker image prune -f
                '''
            }
        }

        stage('Build Application') {
            steps {
                echo "Building Spring Boot application..."
                sh '''
                    chmod +x mvnw
                    ./mvnw clean package -DskipTests
                '''
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
                }
            }
        }

        stage('Run Tests') {
            steps {
                echo "Running tests..."
                sh './mvnw test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "Building Docker image..."
                sh """
                    docker build \
                    --no-cache \
                    -t ${IMAGE_NAME}:${IMAGE_TAG} \
                    -t ${IMAGE_NAME}:latest \
                    .
                """
            }
        }

        stage('Deploy Application') {
            steps {
                echo "Starting containers with docker compose..."

                sh '''
                    docker compose up -d --build
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                echo "Checking running containers..."

                sh '''
                    docker compose ps
                    docker logs gestiondestock-backend --tail 50 || true
                '''
            }
        }

    }

    post {

        success {
            echo "Pipeline completed successfully"
        }

        failure {
            echo "Pipeline failed - showing logs"
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