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
                SPRING_DATASOURCE_DRIVER_CLASS_NAME: com.mysql.cj.jdbc.Driver
                SPRING_JPA_HIBERNATE_DDL_AUTO: update
                SPRING_JPA_SHOW_SQL: true
                SERVER_PORT: 8080

                JWT_SECRET_KEY=+ORJQdAuRJgWSiRMu+3Sq401f0pUMajBZwVRYwno5fiiTp4vxJ42Aiou2tUQipxLknqtEVFStKVB/m9TjJxdPg==

                CLOUDINARY_CLOUD_NAME=dyx1wjvwc
                CLOUDINARY_API_KEY=175456673429688
                CLOUDINARY_API_SECRET=W3WkZgiE1gHDOqfKTqfiNVGiLOE

                SWAGGER_ENABLED=true
                SPRINGDOC_SWAGGER_UI_ENABLED=true
                SPRINGDOC_API_DOCS_ENABLED=true
                SHOW_HEALTH_DETAILS=NEVER

                BUILD_DATE=latest
                BUILD_VERSION=latest

                WATCHTOWER_NOTIFICATION_URL=
                EOF
                '''
            }
        }

        stage('Stop Old Containers') {
            steps {
                script {
                    sh '''
                    echo "=== Vérification des prérequis ==="

                    # Vérifier que docker-compose.prod.yml existe
                    if [ ! -f "docker-compose.prod.yml" ]; then
                        echo "❌ Fichier docker-compose.prod.yml non trouvé!"
                        exit 1
                    fi

                    # Vérifier que MySQL est en cours d'exécution avant de faire le backup
                    if docker ps --format '{{.Names}}' | grep -q "^gestiondestock-mysql$"; then
                        echo "✅ Container MySQL trouvé, préparation du backup..."

                        # Créer le dossier de backup s'il n'existe pas
                        mkdir -p ./backup
                        echo "📁 Dossier de backup créé: ./backup"

                        # Vérifier que la base de données est accessible
                        if docker exec gestiondestock-mysql mysqladmin ping -h localhost -u luca -pluca1593 2>/dev/null; then
                            echo "✅ MySQL est accessible, sauvegarde en cours..."

                            # Exporter la base
                            docker exec gestiondestock-mysql mysqldump -u luca -pluca1593 gestiondestock > ./backup/backup_${IMAGE_TAG}.sql

                            # Vérifier que le backup a été créé
                            if [ -f "./backup/backup_${IMAGE_TAG}.sql" ] && [ -s "./backup/backup_${IMAGE_TAG}.sql" ]; then
                                echo "✅ Backup créé avec succès : backup_${IMAGE_TAG}.sql"
                                # Afficher la taille du backup
                                ls -lh ./backup/backup_${IMAGE_TAG}.sql
                            else
                                echo "⚠️  Le backup est vide ou n'a pas été créé"
                            fi

                            # Garder seulement les 5 derniers backups
                            echo "🧹 Nettoyage des anciens backups..."
                            ls -t ./backup/backup_*.sql 2>/dev/null | tail -n +6 | xargs -r rm
                            echo "✅ Nettoyage terminé"
                        else
                            echo "⚠️  MySQL n'est pas accessible, backup ignoré"
                        fi
                    else
                        echo "⚠️  Container MySQL non trouvé, sauvegarde ignorée"
                    fi

                    echo "=== Arrêt des conteneurs ==="
                    # Vérifier si des conteneurs sont en cours d'exécution
                    if docker compose -f docker-compose.prod.yml ps -q 2>/dev/null | grep -q .; then
                        echo "⏹️  Arrêt des conteneurs existants..."
                        docker compose -f docker-compose.prod.yml down --remove-orphans
                        echo "✅ Conteneurs arrêtés"
                    else
                        echo "ℹ️  Aucun conteneur en cours d'exécution"
                    fi
                    '''
                }
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
                # Démarrer MySQL en premier
                docker compose -f docker-compose.prod.yml up -d mysql
                echo "⏳ Attente de MySQL..."
                for i in 1 2 3 4 5 6 7 8 9 10; do
                    if docker exec gestiondestock-mysql mysqladmin ping -h localhost -u root -prootpassword 2>/dev/null; then
                        echo "✅ MySQL prêt"
                        break
                    fi
                    sleep 5
                done
                # Restaurer AUTO_INCREMENT sur toutes les tables (perdu par les ddl-auto=update successifs)
                echo "🔧 Restauration AUTO_INCREMENT..."
                docker exec gestiondestock-mysql mysql -u root -prootpassword gestiondestock -e "DROP TABLE IF EXISTS flyway_schema_history;" 2>/dev/null
                TABLES=$(docker exec gestiondestock-mysql mysql -u root -prootpassword gestiondestock -NBe "SELECT TABLE_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'gestiondestock' AND COLUMN_NAME = 'id' AND COLUMN_TYPE LIKE '%int%' AND EXTRA NOT LIKE '%auto_increment%';")
                for table in $TABLES; do
                    echo "  → $table"
                    docker exec gestiondestock-mysql mysql -u root -prootpassword gestiondestock -e "SET FOREIGN_KEY_CHECKS=0; ALTER TABLE $table MODIFY id BIGINT NOT NULL AUTO_INCREMENT; SET FOREIGN_KEY_CHECKS=1;" 2>/dev/null
                done
                echo "✅ AUTO_INCREMENT restauré sur ${TABLES:+$(echo "$TABLES" | wc -l)} tables"
                # Démarrer le backend
                docker compose -f docker-compose.prod.yml up -d --build --force-recreate backend
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
                    echo "Waiting for application to start (app takes ~200s)..."
                    sleep 120
                    echo "Checking application health..."
                    for i in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15; do
                        # Vérifier si l'application répond (même si mail health check échoue)
                        HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8085/actuator/health || echo "000")
                        if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "503" ]; then
                            echo "✅ Application is responding (HTTP $HTTP_CODE)"
                            echo "=== Restauration de la base de données ==="
                            # Vérifier si un backup existe
                            if [ -f ./backup/backup_${IMAGE_TAG}.sql ]; then
                                docker exec -i gestiondestock-mysql mysql -u luca -pluca1593 gestiondestock < ./backup/backup_${IMAGE_TAG}.sql
                                echo "✅ Base restaurée depuis backup_${IMAGE_TAG}.sql"
                            else
                                echo "⚠️  Aucun backup trouvé"
                            fi
                            exit 0
                        fi
                        echo "⏳ Waiting for application... attempt $i/15"
                        sleep 20
                    done
                    echo "❌ Application health check failed after 420 seconds"
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