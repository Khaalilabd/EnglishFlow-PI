"""
Endpoints pour les recommandations
"""
from fastapi import APIRouter, HTTPException, status, Query
import logging

from ..schemas.recommendation import (
    RecommendationResponse,
    RecommendationListResponse,
    NewStudentRecommendationRequest
)
from ..services.recommendation_service import RecommendationService

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/recommendation", tags=["Recommendation"])

# Instance du service
recommendation_service = RecommendationService()


@router.get(
    "/courses/{student_id}",
    response_model=RecommendationListResponse,
    summary="Recommander des cours pour un étudiant",
    description="Recommande des cours personnalisés basés sur l'historique de l'étudiant"
)
async def recommend_courses_for_student(
    student_id: str,
    limit: int = Query(5, ge=1, le=20, description="Nombre de recommandations"),
    min_students: int = Query(5, ge=1, description="Nombre minimum d'étudiants par cours")
):
    """
    Recommande des cours pour un étudiant existant.
    
    Utilise le filtrage collaboratif pour trouver des cours similaires
    à ceux suivis par des étudiants ayant un profil similaire.
    
    - **student_id**: ID de l'étudiant
    - **limit**: Nombre de cours à recommander (1-20)
    - **min_students**: Popularité minimale du cours
    """
    try:
        recommendations = recommendation_service.recommend_for_student(
            student_id=student_id,
            n_recommendations=limit,
            min_students=min_students
        )
        
        logger.info(f"Recommandations générées pour étudiant {student_id}: {len(recommendations)} cours")
        
        return RecommendationListResponse(
            student_id=student_id,
            recommendations=recommendations,
            count=len(recommendations)
        )
    except Exception as e:
        logger.error(f"Erreur lors de la recommandation pour {student_id}: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erreur lors de la recommandation: {str(e)}"
        )


@router.post(
    "/courses/new-student",
    response_model=RecommendationListResponse,
    summary="Recommander des cours pour un nouvel étudiant",
    description="Recommande des cours basés sur le profil d'un nouvel étudiant"
)
async def recommend_courses_for_new_student(request: NewStudentRecommendationRequest):
    """
    Recommande des cours pour un nouvel étudiant.
    
    Basé sur:
    - Score moyen de l'étudiant
    - Niveau d'anglais
    - Centres d'intérêt
    
    Retourne des cours adaptés au niveau et aux objectifs de l'étudiant.
    """
    try:
        recommendations = recommendation_service.recommend_for_new_student(request)
        
        logger.info(f"Recommandations générées pour nouvel étudiant: {len(recommendations)} cours")
        
        return RecommendationListResponse(
            student_id=None,
            recommendations=recommendations,
            count=len(recommendations)
        )
    except Exception as e:
        logger.error(f"Erreur lors de la recommandation pour nouvel étudiant: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erreur lors de la recommandation: {str(e)}"
        )


@router.get(
    "/course/{course_code}",
    summary="Détails d'un cours",
    description="Retourne les statistiques et détails d'un cours"
)
async def get_course_details(course_code: str):
    """
    Retourne les détails d'un cours.
    
    Inclut:
    - Score d'interaction moyen
    - Nombre d'étudiants
    - Taux de réussite
    """
    try:
        details = recommendation_service.get_course_details(course_code)
        
        if not details:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"Cours {course_code} non trouvé"
            )
        
        return details
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Erreur lors de la récupération des détails du cours {course_code}: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erreur: {str(e)}"
        )


@router.get(
    "/health",
    summary="État du service de recommandation",
    description="Vérifie que le modèle de recommandation est chargé et fonctionnel"
)
async def recommendation_health():
    """Vérifie l'état du service de recommandation"""
    return {
        "status": "healthy" if recommendation_service.model_loaded else "degraded",
        "model_loaded": recommendation_service.model_loaded,
        "fallback_mode": not recommendation_service.model_loaded
    }
