# Dockerfile pour le ML Service
FROM python:3.11-slim

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers de requirements
COPY requirements.txt .

# Installer les dépendances
RUN pip install --no-cache-dir -r requirements.txt

# Copier le code de l'application
COPY app/ ./app/

# Copier les fichiers de configuration
COPY .env.example .env

# Exposer le port
EXPOSE 8093

# Variables d'environnement
ENV PYTHONUNBUFFERED=1
ENV SERVICE_PORT=8093

# Commande de démarrage
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8093"]
