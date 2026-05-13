"""
Application FastAPI principale - ML Service
"""
from fastapi import FastAPI, status
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
import logging
import sys

from .config import settings
from .api import prediction, clustering, recommendation

# Configuration du logging
logging.basicConfig(
    level=getattr(logging, settings.log_level),
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(sys.stdout)
    ]
)

logger = logging.getLogger(__name__)

# Création de l'application FastAPI
app = FastAPI(
    title="EnglishFlow ML Service",
    description="""
    Service de Machine Learning pour la plateforme EnglishFlow.
    
    ## Fonctionnalités
    
    * **Prédiction**: Prédit le succès/échec des étudiants
    * **Clustering**: Segmente les étudiants en groupes homogènes
    * **Recommandation**: Recommande des cours personnalisés
    
    ## Modèles ML
    
    - **Prédiction**: Random Forest Classifier (91% accuracy)
    - **Clustering**: K-Means (3 clusters)
    - **Recommandation**: Filtrage collaboratif
    """,
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc",
    openapi_url="/openapi.json"
)

# Configuration CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origins_list,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# Routes principales
@app.get("/", tags=["Root"])
async def root():
    """Page d'accueil de l'API"""
    return {
        "service": "EnglishFlow ML Service",
        "version": "1.0.0",
        "status": "running",
        "documentation": "/docs",
        "endpoints": {
            "prediction": f"{settings.api_prefix}/prediction",
            "clustering": f"{settings.api_prefix}/clustering",
            "recommendation": f"{settings.api_prefix}/recommendation"
        }
    }


@app.get("/health", tags=["Health"])
async def health_check():
    """Endpoint de santé"""
    from .services import PredictionService, ClusteringService, RecommendationService
    
    # Vérifier l'état des services
    prediction_service = PredictionService()
    clustering_service = ClusteringService()
    recommendation_service = RecommendationService()
    
    services_status = {
        "prediction": prediction_service.model_loaded,
        "clustering": clustering_service.model_loaded,
        "recommendation": recommendation_service.model_loaded
    }
    
    all_healthy = all(services_status.values())
    
    return {
        "status": "healthy" if all_healthy else "degraded",
        "services": services_status,
        "message": "All services operational" if all_healthy else "Some services are degraded"
    }


@app.get("/info", tags=["Info"])
async def service_info():
    """Informations sur le service"""
    return {
        "service_name": settings.service_name,
        "service_port": settings.service_port,
        "api_prefix": settings.api_prefix,
        "api_version": settings.api_version,
        "models_path": settings.models_path,
        "eureka_server": settings.eureka_server_url
    }


# Inclusion des routers
app.include_router(
    prediction.router,
    prefix=settings.api_prefix,
)

app.include_router(
    clustering.router,
    prefix=settings.api_prefix,
)

app.include_router(
    recommendation.router,
    prefix=settings.api_prefix,
)


# Gestionnaire d'erreurs global
@app.exception_handler(Exception)
async def global_exception_handler(request, exc):
    """Gestionnaire d'erreurs global"""
    logger.error(f"Erreur non gérée: {exc}", exc_info=True)
    return JSONResponse(
        status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
        content={
            "error": "Internal Server Error",
            "message": str(exc)
        }
    )


# Événements de démarrage et d'arrêt
@app.on_event("startup")
async def startup_event():
    """Événement de démarrage"""
    logger.info("=" * 60)
    logger.info("🚀 Démarrage du ML Service")
    logger.info("=" * 60)
    logger.info(f"Service: {settings.service_name}")
    logger.info(f"Port: {settings.service_port}")
    logger.info(f"API Prefix: {settings.api_prefix}")
    logger.info(f"CORS Origins: {settings.cors_origins_list}")
    logger.info(f"Models Path: {settings.models_path}")
    logger.info("=" * 60)
    
    # Charger les modèles au démarrage
    from .services import PredictionService, ClusteringService
    
    logger.info("📦 Chargement des modèles ML...")
    prediction_service = PredictionService()
    clustering_service = ClusteringService()
    
    logger.info(f"✅ Prédiction: {'Chargé' if prediction_service.model_loaded else '❌ Non chargé'}")
    logger.info(f"✅ Clustering: {'Chargé' if clustering_service.model_loaded else '❌ Non chargé'}")
    logger.info("⚠️  Recommandation: Désactivé (modèle trop volumineux - chargement à la demande)")
    logger.info("=" * 60)
    logger.info("✅ ML Service démarré avec succès!")
    logger.info(f"📍 Documentation: http://{settings.service_host}:{settings.service_port}/docs")
    logger.info("=" * 60)


@app.on_event("shutdown")
async def shutdown_event():
    """Événement d'arrêt"""
    logger.info("🛑 Arrêt du ML Service")


if __name__ == "__main__":
    import uvicorn
    
    uvicorn.run(
        "app.main:app",
        host=settings.service_host,
        port=settings.service_port,
        reload=True,
        log_level=settings.log_level.lower()
    )
