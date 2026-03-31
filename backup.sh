#!/bin/bash
set -e

echo "========================================="
echo "  Gestion de Stock - Database Backup"
echo "========================================="

BACKUP_DIR="./backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="${BACKUP_DIR}/backup-${TIMESTAMP}.sql.gz"
COMPOSE_FILE="docker-compose.prod.yml"

mkdir -p "$BACKUP_DIR"

echo "Starting database backup..."

if docker-compose -f "$COMPOSE_FILE" ps mysql | grep -q "Up"; then
    docker-compose -f "$COMPOSE_FILE" exec -T mysql \
        mysqldump -u"${DB_USERNAME}" -p"${DB_PASSWORD}" \
        --single-transaction --routines --triggers --events \
        "${DB_NAME}" | gzip > "$BACKUP_FILE"
    
    BACKUP_SIZE=$(du -h "$BACKUP_FILE" | cut -f1)
    echo "Backup completed: $BACKUP_FILE ($BACKUP_SIZE)"
else
    echo "Error: MySQL container is not running"
    exit 1
fi

# Keep only last 30 backups
ls -t "${BACKUP_DIR}"/backup-*.sql.gz 2>/dev/null | tail -n +31 | xargs -r rm
echo "Old backups cleaned (keeping last 30)"
