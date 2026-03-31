#!/bin/bash
set -e

echo "========================================="
echo "  Gestion de Stock - Database Restore"
echo "========================================="

if [ -z "$1" ]; then
    echo "Usage: $0 <backup-file.sql.gz>"
    echo ""
    echo "Available backups:"
    ls -lh ./backups/backup-*.sql.gz 2>/dev/null || echo "  No backups found"
    exit 1
fi

BACKUP_FILE="$1"
COMPOSE_FILE="docker-compose.prod.yml"

if [ ! -f "$BACKUP_FILE" ]; then
    echo "Error: Backup file not found: $BACKUP_FILE"
    exit 1
fi

echo "WARNING: This will OVERWRITE the current database!"
read -p "Are you sure? (yes/no): " CONFIRM

if [ "$CONFIRM" != "yes" ]; then
    echo "Restore cancelled"
    exit 0
fi

echo "Restoring database from: $BACKUP_FILE"

docker-compose -f "$COMPOSE_FILE" exec -T mysql \
    mysql -u"${DB_USERNAME}" -p"${DB_PASSWORD}" "${DB_NAME}" < <(gunzip -c "$BACKUP_FILE")

echo "Database restored successfully!"
