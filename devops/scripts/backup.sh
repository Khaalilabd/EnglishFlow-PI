#!/bin/bash
# Database Backup Script
set -e
GREEN='\033[0;32m'
NC='\033[0m'
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BACKUP_DIR="$PROJECT_DIR/backups"
mkdir -p "$BACKUP_DIR"
BACKUP_FILE="$BACKUP_DIR/backup-$(date +%Y%m%d-%H%M%S).sql"
echo -e "${GREEN}[INFO]${NC} Creating backup..."
cd "$PROJECT_DIR"
docker-compose exec -T postgres pg_dumpall -U postgres > "$BACKUP_FILE"
gzip "$BACKUP_FILE"
echo -e "${GREEN}[INFO]${NC} Backup created: ${BACKUP_FILE}.gz"
ls -lh "$BACKUP_DIR"
