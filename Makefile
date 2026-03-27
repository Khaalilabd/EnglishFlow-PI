.PHONY: help dev prod staging build start stop restart logs clean backup restore

help:
	@echo "EnglishFlow - Available commands:"
	@echo "  make dev        - Start development environment"
	@echo "  make prod       - Start production environment"
	@echo "  make staging    - Start staging environment"
	@echo "  make build      - Build all Docker images"
	@echo "  make start      - Start all services"
	@echo "  make stop       - Stop all services"
	@echo "  make restart    - Restart all services"
	@echo "  make logs       - Show logs (all services)"
	@echo "  make clean      - Clean up containers and volumes"
	@echo "  make backup     - Backup database"
	@echo "  make restore    - Restore database"

dev:
	@chmod +x scripts/deploy.sh
	@./scripts/deploy.sh dev

prod:
	@chmod +x scripts/deploy.sh
	@./scripts/deploy.sh prod

staging:
	@chmod +x scripts/deploy.sh
	@./scripts/deploy.sh staging

build:
	docker-compose build --parallel

start:
	docker-compose up -d

stop:
	@chmod +x scripts/stop.sh
	@./scripts/stop.sh

restart: stop start

logs:
	@chmod +x scripts/logs.sh
	@./scripts/logs.sh

clean:
	docker-compose down -v
	docker system prune -f

backup:
	@chmod +x scripts/backup.sh
	@./scripts/backup.sh

restore:
	@chmod +x scripts/restore.sh
	@./scripts/restore.sh
