"""
Configuration du service ML
"""
from pydantic_settings import BaseSettings
from typing import List


class Settings(BaseSettings):
    """Configuration de l'application"""
    
    # Service
    service_name: str = "ml-service"
    service_port: int = 8093
    service_host: str = "0.0.0.0"
    
    # Eureka
    eureka_server_url: str = "http://localhost:8761/eureka"
    eureka_instance_hostname: str = "localhost"
    eureka_instance_port: int = 8093
    
    # Microservices
    auth_service_url: str = "http://localhost:8081"
    courses_service_url: str = "http://localhost:8082"
    learning_service_url: str = "http://localhost:8083"
    gamification_service_url: str = "http://localhost:8088"
    
    # ML Models
    models_path: str = "./app/models"
    
    # API
    api_prefix: str = "/api/ml"
    api_version: str = "v1"
    
    # CORS
    cors_origins: str = "http://localhost:4200,http://localhost:8080"
    
    # Logging
    log_level: str = "INFO"
    
    class Config:
        env_file = ".env"
        case_sensitive = False
    
    @property
    def cors_origins_list(self) -> List[str]:
        """Retourne la liste des origines CORS"""
        return [origin.strip() for origin in self.cors_origins.split(",")]


settings = Settings()
