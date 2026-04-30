#!/bin/bash
# Database Restore Script
set -e
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'
BACKUP_FILE=$1
if [[ -z "$BACKUP_FILE" ]]; then
    echo -e "${RED}[ERROR]${NC} Usage: ./scripts/restore.sh <backup-file.sql.gz>"
    exit 1
fi
if [[ ! -f "$BACKUP_FILE" ]]; then
    echo -e "${RED}[ERROR]${NC} Backup file not found: $BACKUP_FILE"
    exit 1
fi
echo -e "${GREEN}[INFO]${NC} Restoring from: $BACKUP_FILE"
gunzip -c "$BACKUP_FILE" | docker-compose exec -T postgres psql -U postgres
echo -e "${GREEN}[INFO]${NC} Restore completed"
