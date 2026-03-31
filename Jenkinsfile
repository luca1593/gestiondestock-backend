pipeline {
    agent any

    environment {
        APP_NAME = 'gestiondestock-backend'
        IMAGE_NAME = 'gestiondestock-backend'
        IMAGE_TAG = "${env.BUILD_NUMBER ?: 'latest'}"
        BUILD_TIMESTAMP = sh(script: 'date +%Y%m%d_%H%M%S', returnStdout: true).trim()
        DOCKER_REGISTRY = ''
        COMPOSE_PROJECT_NAME = 'gestiondestock'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo "Source code checked out successfully"
            }
        }

        stage('Clean Old Images') {
            steps {
                echo "Cleaning old Docker images to free space..."
                sh '''
                    docker image prune -f
                    docker images "${IMAGE_NAME}" --format "{{.ID}}" | tail -n +6 | xargs -r docker rmi -f 2>/dev/null || true
                '''
            }
        }

        stage('Build') {
            steps {
                echo "Building the application..."
                sh '''
                    chmod +x mvnw
                    ./mvnw clean package -DskipTests
                '''
            }
            post {
                success {
                    echo "Build successful"
                    archiveArtifacts artifacts: '**/target/*.war', fingerprint: true
                }
                failure {
                    echo "Build failed!"
                    error "Build stage failed"
                }
            }
        }

        stage('Test') {
            steps {
                echo "Running tests..."
                sh './mvnw test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/TEST-*.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "Building Docker image with latest changes..."
                sh """
                    docker build \
                        --no-cache \
                        --build-arg BUILD_DATE=${BUILD_TIMESTAMP} \
                        --build-arg BUILD_VERSION=${IMAGE_TAG} \
                        -t ${IMAGE_NAME}:${IMAGE_TAG} \
                        -t ${IMAGE_NAME}:latest \
                        .
                """
            }
            post {
                success {
                    echo "Docker image built successfully: ${IMAGE_NAME}:${IMAGE_TAG}"
                    sh "docker images ${IMAGE_NAME}"
                }
                failure {
                    echo "Docker image build failed!"
                    error "Docker build stage failed"
                }
            }
        }

        stage('Deploy') {
            steps {
                echo "Deploying with docker-compose..."
                sh '''
                    docker-compose down || true
                    docker-compose up -d mysql
                    echo "Waiting for MySQL to be ready..."
                    sleep 15
                    docker-compose up -d --build backend
                '''
            }
            post {
                success {
                    echo "Application deployed successfully"
                }
                failure {
                    echo "Deployment failed!"
                    sh 'docker-compose logs backend'
                    error "Deploy stage failed"
                }
            }
        }

        stage('Health Check') {
            steps {
                echo "Checking application health..."
                sh '''
                    MAX_RETRIES=30
                    RETRY_COUNT=0
                    until curl -f http://localhost:8080/swagger-ui.html || [ $RETRY_COUNT -eq $MAX_RETRIES ]; do
                        echo "Waiting for application to start... ($((RETRY_COUNT+1))/$MAX_RETRIES)"
                        sleep 5
                        RETRY_COUNT=$((RETRY_COUNT+1))
                    done
                    if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
                        echo "Application failed to start within timeout"
                        docker-compose logs backend
                        exit 1
                    fi
                    echo "Application is running!"
                '''
            }
        }

        stage('Verify Image') {
            steps {
                echo "Verifying deployed image..."
                sh """
                    echo "Running container image:"
                    docker inspect --format='{{.Config.Image}}' gestiondestock-backend || true
                    echo "Available images:"
                    docker images ${IMAGE_NAME}
                """
            }
        }
    }

    post {
        always {
            echo "Cleaning up workspace..."
            cleanWs()
        }
        success {
            echo "Pipeline completed successfully!"
        }
        failure {
            echo "Pipeline failed!"
        }
    }
}
