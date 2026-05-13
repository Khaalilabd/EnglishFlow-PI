"""
Endpoints pour le clustering
"""
from fastapi import APIRouter, HTTPException, status
import logging

from ..schemas.clustering import (
    ClusteringRequest,
    ClusteringResponse,
    ClusterAnalysisResponse
)
from ..services.clustering_service import ClusteringService

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/clustering", tags=["Clustering"])

# Instance du service
clustering_service = ClusteringService()


@router.post(
    "/student",
    response_model=ClusteringResponse,
    summary="Identifier le cluster d'un étudiant",
    description="Identifie le groupe auquel appartient un étudiant basé sur son profil"
)
async def identify_student_cluster(request: ClusteringRequest):
    """
    Identifie le cluster d'un étudiant.
    
    Les étudiants sont regroupés en clusters homogènes selon leurs caractéristiques:
    - Cluster 0: Étudiants Performants
    - Cluster 1: Étudiants Moyens
    - Cluster 2: Étudiants À Risque
    """
    try:
        result = clustering_service.predict_cluster(request)
        logger.info(f"Étudiant assigné au cluster {result.cluster}: {result.cluster_label}")
        return result
    except Exception as e:
        logger.error(f"Erreur lors du clustering: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erreur lors du clustering: {str(e)}"
        )


@router.post(
    "/student/{student_id}",
    response_model=ClusteringResponse,
    summary="Identifier le cluster d'un étudiant spécifique",
    description="Identifie le groupe d'un étudiant identifié"
)
async def identify_cluster_by_id(student_id: str, request: ClusteringRequest):
    """
    Identifie le cluster d'un étudiant spécifique.
    """
    try:
        result = clustering_service.predict_cluster(request, student_id=student_id)
        logger.info(f"Étudiant {student_id} assigné au cluster {result.cluster}")
        return result
    except Exception as e:
        logger.error(f"Erreur lors du clustering pour {student_id}: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erreur lors du clustering: {str(e)}"
        )


@router.get(
    "/analysis",
    response_model=ClusterAnalysisResponse,
    summary="Analyse globale des clusters",
    description="Retourne une analyse complète de tous les clusters"
)
async def get_cluster_analysis():
    """
    Retourne une analyse globale des clusters.
    
    Inclut:
    - Nombre de clusters
    - Caractéristiques de chaque cluster
    - Statistiques globales
    """
    try:
        analysis = clustering_service.get_cluster_analysis()
        
        if not analysis:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Analyse des clusters non disponible"
            )
        
        return ClusterAnalysisResponse(
            n_clusters=analysis['n_clusters'],
            silhouette_score=0.45,  # Valeur par défaut, à remplacer par la vraie valeur
            clusters=analysis['clusters']
        )
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Erreur lors de l'analyse des clusters: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erreur: {str(e)}"
        )


@router.get(
    "/health",
    summary="État du service de clustering",
    description="Vérifie que le modèle de clustering est chargé et fonctionnel"
)
async def clustering_health():
    """Vérifie l'état du service de clustering"""
    return {
        "status": "healthy" if clustering_service.model_loaded else "degraded",
        "model_loaded": clustering_service.model_loaded,
        "n_clusters": len(clustering_service.CLUSTER_LABELS) if clustering_service.model_loaded else 0
    }
