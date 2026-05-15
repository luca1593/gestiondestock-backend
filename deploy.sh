#!/bin/bash
set -e

echo "========================================="
echo "  Gestion de Stock - Production Deploy"
echo "========================================="
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Configuration
COMPOSE_FILE="docker-compose.prod.yml"
ENV_FILE=".env"
BACKUP_DIR="./backups"
LOG_FILE="./deploy-$(date +%Y%m%d_%H%M%S).log"

# Functions
log() {
    echo -e "${GREEN}[$(date '+%H:%M:%S')]${NC} $1" | tee -a "$LOG_FILE"
}

warn() {
    echo -e "${YELLOW}[$(date '+%H:%M:%S')] WARNING:${NC} $1" | tee -a "$LOG_FILE"
}

error() {
    echo -e "${RED}[$(date '+%H:%M:%S')] ERROR:${NC} $1" | tee -a "$LOG_FILE"
    exit 1
}

# Check prerequisites
check_prerequisites() {
    log "Checking prerequisites..."
    
    command -v docker >/dev/null 2>&1 || error "Docker is not installed"
    command -v docker-compose >/dev/null 2>&1 || error "Docker Compose is not installed"
    
    if [ ! -f "$ENV_FILE" ]; then
        error ".env file not found. Copy .env.example to .env and configure it."
    fi
    
    log "Prerequisites OK"
}

# Backup database
backup_database() {
    log "Backing up database..."
    mkdir -p "$BACKUP_DIR"
    
    if docker-compose -f "$COMPOSE_FILE" ps mysql | grep -q "Up"; then
        docker-compose -f "$COMPOSE_FILE" exec -T mysql \
            mysqldump -u"${DB_USERNAME}" -p"${DB_PASSWORD}" "${DB_NAME}" \
            > "${BACKUP_DIR}/backup-$(date +%Y%m%d_%H%M%S).sql" 2>/dev/null || warn "Backup failed, continuing..."
        log "Database backup completed"
    else
        warn "MySQL not running, skipping backup"
    fi
}

# Pull latest changes
pull_latest() {
    log "Pulling latest changes..."
    git pull origin main 2>/dev/null || warn "Not a git repo or pull failed"
}

# Build and deploy
deploy() {
    log "Building and deploying..."
    
    export BUILD_DATE=$(date +%Y%m%d_%H%M%S)
    export BUILD_VERSION=$(git describe --tags --always 2>/dev/null || echo "latest")
    
    docker-compose -f "$COMPOSE_FILE" build --no-cache backend || error "Build failed"
    
    log "Starting services..."
    docker-compose -f "$COMPOSE_FILE" up -d mysql
    log "Waiting for MySQL..."
    sleep 15
    
    docker-compose -f "$COMPOSE_FILE" up -d backend
    log "Waiting for backend (app takes ~136s)..."
    sleep 90
    
    docker-compose -f "$COMPOSE_FILE" up -d nginx
    
    log "All services started"
}

# Health check
health_check() {
    log "Running health checks..."
    
    MAX_RETRIES=20
    RETRY_COUNT=0
    
    while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
        if curl -sf http://localhost/actuator/health >/dev/null 2>&1; then
            log "Application is healthy!"
            return 0
        fi
        RETRY_COUNT=$((RETRY_COUNT+1))
        log "Waiting for application... ($RETRY_COUNT/$MAX_RETRIES)"
        sleep 5
    done
    
    error "Application failed to start within timeout"
}

# Cleanup old images
cleanup() {
    log "Cleaning up old Docker images..."
    docker image prune -f >/dev/null 2>&1
    log "Cleanup completed"
}

# Show status
show_status() {
    echo ""
    log "========================================="
    log "  Deployment Summary"
    log "========================================="
    docker-compose -f "$COMPOSE_FILE" ps
    echo ""
    log "Access points:"
    log "  - Application: https://localhost"
    log "  - phpMyAdmin:  https://localhost:8081"
    log "  - API Docs:    https://localhost/swagger-ui.html"
    log "========================================="
}

# Main
main() {
    check_prerequisites
    backup_database
    pull_latest
    deploy
    health_check
    cleanup
    show_status
    
    log "Deployment completed successfully!"
}

main "$@"
