.PHONY: help dev prod staging build start stop restart logs clean backup restore test test-backend test-frontend check-health docker-build setup-env sonar quality-check

help:
	@echo "EnglishFlow - Available commands:"
	@echo ""
	@echo "🚀 Environment Management:"
	@echo "  make dev              - Start development environment"
	@echo "  make prod             - Start production environment"
	@echo "  make staging          - Start staging environment"
	@echo "  make setup-env        - Generate .env files from templates"
	@echo ""
	@echo "🐳 Docker Commands:"
	@echo "  make build            - Build all Docker images"
	@echo "  make docker-build     - Build all Docker images with custom tag"
	@echo "  make start            - Start all services"
	@echo "  make stop             - Stop all services"
	@echo "  make restart          - Restart all services"
	@echo "  make logs             - Show logs (all services)"
	@echo "  make clean            - Clean up containers and volumes"
	@echo ""
	@echo "🧪 Testing:"
	@echo "  make test             - Run all tests (backend + frontend)"
	@echo "  make test-backend     - Run backend tests only"
	@echo "  make test-frontend    - Run frontend tests only"
	@echo "  make test-docker      - Run tests in Docker"
	@echo ""
	@echo "📊 Quality & Monitoring:"
	@echo "  make sonar            - Run SonarCloud analysis"
	@echo "  make quality-check    - Run quality gates checks"
	@echo "  make check-health     - Check health of all services"
	@echo "  make coverage         - Generate coverage reports"
	@echo ""
	@echo "💾 Database:"
	@echo "  make backup           - Backup database"
	@echo "  make restore          - Restore database"
	@echo ""
	@echo "📚 Documentation:"
	@echo "  make docs             - Open DevOps documentation"

dev:
	@echo "🚀 Starting development environment..."
	@cd devops/docker && docker-compose up -d
	@echo "✅ Development environment started!"
	@echo "   - Eureka: http://localhost:8761"
	@echo "   - API Gateway: http://localhost:8080"
	@echo "   - Frontend: http://localhost:4200"
	@echo "   - Grafana: http://localhost:3000"

prod:
	@echo "🚀 Starting production environment..."
	@docker-compose -f docker-compose.production.yml up -d
	@echo "✅ Production environment started!"

staging:
	@echo "🚀 Starting staging environment..."
	@cd devops/docker && docker-compose -f docker-compose.staging.yml up -d
	@echo "✅ Staging environment started!"

build:
	@echo "🐳 Building all Docker images..."
	@cd devops/docker && docker-compose build --parallel
	@echo "✅ All images built!"

docker-build:
	@echo "🐳 Building all Docker images with custom tag..."
	@chmod +x devops/scripts/docker-build-all.sh
	@./devops/scripts/docker-build-all.sh $(TAG)

start:
	@echo "▶️  Starting all services..."
	@cd devops/docker && docker-compose up -d
	@echo "✅ All services started!"

stop:
	@echo "⏹️  Stopping all services..."
	@cd devops/docker && docker-compose down
	@echo "✅ All services stopped!"

restart: stop start

logs:
	@cd devops/docker && docker-compose logs -f

clean:
	@echo "🧹 Cleaning up..."
	@cd devops/docker && docker-compose down -v
	@docker system prune -f
	@echo "✅ Cleanup complete!"

# Testing
test:
	@echo "🧪 Running all tests..."
	@chmod +x devops/scripts/run-tests.sh
	@./devops/scripts/run-tests.sh all

test-backend:
	@echo "🧪 Running backend tests..."
	@chmod +x devops/scripts/run-tests.sh
	@./devops/scripts/run-tests.sh backend

test-frontend:
	@echo "🧪 Running frontend tests..."
	@chmod +x devops/scripts/run-tests.sh
	@./devops/scripts/run-tests.sh frontend

test-docker:
	@echo "🧪 Running tests in Docker..."
	@docker-compose -f docker-compose.test.yml up --abort-on-container-exit
	@docker-compose -f docker-compose.test.yml down

# Quality & Monitoring
sonar:
	@echo "📊 Running SonarCloud analysis..."
	@cd backend && mvn clean verify sonar:sonar \
		-Dsonar.projectKey=englishflow-backend \
		-Dsonar.organization=your-org-name \
		-Dsonar.host.url=https://sonarcloud.io
	@echo "✅ SonarCloud analysis complete!"

quality-check:
	@echo "🔍 Running quality checks..."
	@chmod +x devops/scripts/run-tests.sh
	@./devops/scripts/run-tests.sh all
	@echo "✅ Quality checks passed!"

check-health:
	@echo "🏥 Checking service health..."
	@chmod +x devops/scripts/check-services.sh
	@./devops/scripts/check-services.sh

coverage:
	@echo "📊 Generating coverage reports..."
	@cd backend && for service in auth-service courses-service community-service messaging-service club-service event-service learning-service complaints-service sponsors-service; do \
		if [ -d $$service ]; then \
			cd $$service && mvn jacoco:report && cd .. ; \
		fi \
	done
	@cd frontend && npm run test -- --code-coverage --watch=false
	@echo "✅ Coverage reports generated!"

# Database
backup:
	@echo "💾 Backing up database..."
	@chmod +x devops/scripts/backup.sh
	@./devops/scripts/backup.sh
	@echo "✅ Backup complete!"

restore:
	@echo "♻️  Restoring database..."
	@chmod +x devops/scripts/restore.sh
	@./devops/scripts/restore.sh
	@echo "✅ Restore complete!"

# Setup
setup-env:
	@echo "🔧 Generating .env files..."
	@chmod +x devops/scripts/generate-env.sh
	@./devops/scripts/generate-env.sh

# Documentation
docs:
	@echo "📚 Opening DevOps documentation..."
	@open DEVOPS_SETUP.md || xdg-open DEVOPS_SETUP.md || cat DEVOPS_SETUP.md
