pipeline {
    agent any

    environment {
        APP_NAME = 'gestiondestock-backend'
        IMAGE_NAME = 'gestiondestock-backend'
        IMAGE_TAG = "${env.BUILD_NUMBER ?: 'latest'}"
        BUILD_TIMESTAMP = sh(script: 'date +%Y%m%d_%H%M%S', returnStdout: true).trim()
        DOCKER_REGISTRY = ''
        COMPOSE_PROJECT_NAME = 'gestiondestock'
        
        // Credentials from Jenkins (configure in Jenkins credentials)
        DB_HOST = 'mysql'
        DB_PORT = '3307'
        DB_NAME = 'gestiondestock'
        DB_USERNAME = credentials('DB_USERNAME')
        DB_PASSWORD = credentials('DB_PASSWORD')
        JWT_SECRET_KEY = credentials('JWT_SECRET_KEY')
        FLICKR_API_KEY = credentials('FLICKR_API_KEY')
        FLICKR_API_SECRET = credentials('FLICKR_API_SECRET')
        FLICKR_APP_KEY = credentials('FLICKR_APP_KEY')
        FLICKR_APP_SECRET = credentials('FLICKR_APP_SECRET')
        MAIL_USERNAME = credentials('MAIL_USERNAME')
        MAIL_PASSWORD = credentials('MAIL_PASSWORD')
        MYSQL_ROOT_PASSWORD = credentials('MYSQL_ROOT_PASSWORD')
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
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
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
                echo "Deploying with docker compose..."
                sh '''
                    docker compose down || true
                    docker compose up -d mysql
                    echo "Waiting for MySQL to be ready..."
                    for i in {1..30}; do
                        if docker compose exec -T mysql mysqladmin ping -h localhost -u root -p${MYSQL_ROOT_PASSWORD} --silent 2>/dev/null; then
                            echo "MySQL is ready!"
                            break
                        fi
                        echo "Waiting for MySQL... ($i/30)"
                        sleep 2
                    done
                    docker compose up -d backend
                    echo "Backend container started, waiting for application..."
                '''
            }
            post {
                success {
                    echo "Application deployed successfully"
                }
                failure {
                    echo "Deployment failed!"
                    sh 'docker compose logs backend || true'
                    error "Deploy stage failed"
                }
            }
        }

        stage('Health Check') {
            steps {
                echo "Checking application health..."
                sh '''
                    echo "Waiting for backend to start..."
                    for i in {1..60}; do
                        if curl -sf http://localhost:8085/actuator/health > /dev/null 2>&1; then
                            echo "Application is healthy!"
                            curl -s http://localhost:8085/actuator/health
                            exit 0
                        fi
                        CONTAINER_STATUS=$(docker compose ps backend 2>/dev/null | tail -1 | awk '{print $4}' || echo "unknown")
                        echo "Waiting for application to start... ($i/60) - Container status: $CONTAINER_STATUS"
                        sleep 3
                    done
                    echo "Application failed to start within timeout"
                    echo "=== Backend Logs ==="
                    docker compose logs backend || true
                    echo "=== Docker PS ==="
                    docker compose ps || true
                    exit 1
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
                    echo "Container status:"
                    docker compose ps
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
