"""
Endpoints pour les prédictions
"""
from fastapi import APIRouter, HTTPException, status
from typing import List
import logging

from ..schemas.prediction import (
    PredictionRequest,
    PredictionResponse,
    BatchPredictionRequest,
    BatchPredictionResponse
)
from ..services.prediction_service import PredictionService

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/prediction", tags=["Prediction"])

# Instance du service
prediction_service = PredictionService()


@router.post(
    "/student",
    response_model=PredictionResponse,
    summary="Prédire le succès d'un étudiant",
    description="Prédit la probabilité de succès d'un étudiant basé sur ses données d'engagement"
)
async def predict_student_success(request: PredictionRequest):
    """
    Prédit le succès d'un étudiant.
    
    - **num_of_prev_attempts**: Nombre de tentatives précédentes
    - **studied_credits**: Crédits étudiés
    - **total_clicks**: Total de clics sur la plateforme
    - **avg_score**: Score moyen aux évaluations
    
    Retourne une prédiction avec probabilité et recommandations.
    """
    try:
        result = prediction_service.predict_single(request)
        logger.info(f"Prédiction effectuée: {result.prediction_label} (confiance: {result.confidence:.2f})")
        return result
    except Exception as e:
        logger.error(f"Erreur lors de la prédiction: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erreur lors de la prédiction: {str(e)}"
        )


@router.post(
    "/student/{student_id}",
    response_model=PredictionResponse,
    summary="Prédire le succès d'un étudiant spécifique",
    description="Prédit la probabilité de succès pour un étudiant identifié"
)
async def predict_student_by_id(student_id: str, request: PredictionRequest):
    """
    Prédit le succès d'un étudiant identifié par son ID.
    """
    try:
        result = prediction_service.predict_single(request, student_id=student_id)
        logger.info(f"Prédiction pour étudiant {student_id}: {result.prediction_label}")
        return result
    except Exception as e:
        logger.error(f"Erreur lors de la prédiction pour {student_id}: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erreur lors de la prédiction: {str(e)}"
        )


@router.post(
    "/batch",
    response_model=BatchPredictionResponse,
    summary="Prédictions en batch",
    description="Prédit le succès pour plusieurs étudiants en une seule requête"
)
async def predict_batch(request: BatchPredictionRequest):
    """
    Prédit le succès pour plusieurs étudiants.
    
    Utile pour analyser une cohorte complète d'étudiants.
    """
    try:
        if not request.students:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="La liste des étudiants ne peut pas être vide"
            )
        
        result = prediction_service.predict_batch(request.students)
        logger.info(f"Prédictions batch effectuées pour {result['count']} étudiants")
        
        return BatchPredictionResponse(**result)
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Erreur lors des prédictions batch: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erreur lors des prédictions: {str(e)}"
        )


@router.get(
    "/feature-importance",
    summary="Importance des variables",
    description="Retourne l'importance de chaque variable dans le modèle de prédiction"
)
async def get_feature_importance():
    """
    Retourne l'importance des features utilisées par le modèle.
    
    Utile pour comprendre quels facteurs influencent le plus la prédiction.
    """
    try:
        importance = prediction_service.get_feature_importance()
        
        if not importance:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Importance des features non disponible"
            )
        
        # Trier par importance décroissante
        sorted_importance = dict(sorted(importance.items(), key=lambda x: x[1], reverse=True))
        
        return {
            "feature_importance": sorted_importance,
            "top_3_features": list(sorted_importance.keys())[:3]
        }
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Erreur lors de la récupération de l'importance: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erreur: {str(e)}"
        )


@router.get(
    "/health",
    summary="État du service de prédiction",
    description="Vérifie que le modèle de prédiction est chargé et fonctionnel"
)
async def prediction_health():
    """Vérifie l'état du service de prédiction"""
    return {
        "status": "healthy" if prediction_service.model_loaded else "degraded",
        "model_loaded": prediction_service.model_loaded,
        "model_type": prediction_service.model.model_type if prediction_service.model_loaded else None
    }
