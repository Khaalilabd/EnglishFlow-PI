"""
Schémas pour les recommandations
"""
from pydantic import BaseModel, Field
from typing import List, Optional


class RecommendationResponse(BaseModel):
    """Réponse de recommandation de cours"""
    course_code: str = Field(description="Code du cours")
    course_name: Optional[str] = Field(None, description="Nom du cours")
    recommendation_score: float = Field(ge=0, le=1, description="Score de recommandation")
    avg_interaction: float = Field(description="Score d'interaction moyen")
    nb_students: int = Field(description="Nombre d'étudiants ayant suivi le cours")
    success_rate: float = Field(ge=0, le=1, description="Taux de réussite")
    reason: Optional[str] = Field(None, description="Raison de la recommandation")
    
    class Config:
        json_schema_extra = {
            "example": {
                "course_code": "AAA",
                "course_name": "Advanced Analytics",
                "recommendation_score": 0.85,
                "avg_interaction": 4.2,
                "nb_students": 1500,
                "success_rate": 0.75,
                "reason": "Cours adapté à votre niveau élevé"
            }
        }


class NewStudentRecommendationRequest(BaseModel):
    """Requête de recommandation pour un nouvel étudiant"""
    avg_score: float = Field(ge=0, le=100, description="Score moyen de l'étudiant")
    studied_credits: Optional[int] = Field(None, ge=0, description="Crédits étudiés")
    english_level: Optional[str] = Field(None, description="Niveau d'anglais (A1-C2)")
    interests: Optional[List[str]] = Field(None, description="Centres d'intérêt")
    n_recommendations: int = Field(5, ge=1, le=20, description="Nombre de recommandations")
    
    class Config:
        json_schema_extra = {
            "example": {
                "avg_score": 75.0,
                "studied_credits": 30,
                "english_level": "B2",
                "interests": ["technology", "business"],
                "n_recommendations": 5
            }
        }


class RecommendationListResponse(BaseModel):
    """Liste de recommandations"""
    student_id: Optional[str] = None
    recommendations: List[RecommendationResponse] = Field(description="Liste des cours recommandés")
    count: int = Field(description="Nombre de recommandations")
    
    class Config:
        json_schema_extra = {
            "example": {
                "student_id": "12345",
                "recommendations": [
                    {
                        "course_code": "AAA",
                        "recommendation_score": 0.85,
                        "avg_interaction": 4.2,
                        "nb_students": 1500,
                        "success_rate": 0.75
                    }
                ],
                "count": 5
            }
        }
