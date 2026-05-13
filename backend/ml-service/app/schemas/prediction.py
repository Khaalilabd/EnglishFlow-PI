"""
Schémas pour les prédictions
"""
from pydantic import BaseModel, Field
from typing import List, Optional


class PredictionRequest(BaseModel):
    """Requête de prédiction pour un étudiant"""
    num_of_prev_attempts: int = Field(ge=0, description="Nombre de tentatives précédentes")
    studied_credits: int = Field(ge=0, description="Crédits étudiés")
    total_clicks: int = Field(ge=0, description="Total de clics")
    nb_sessions: int = Field(ge=0, description="Nombre de sessions")
    avg_clicks: float = Field(ge=0, description="Clics moyens par session")
    max_clicks: int = Field(ge=0, description="Maximum de clics en une session")
    avg_score: float = Field(ge=0, le=100, description="Score moyen")
    min_score: float = Field(ge=0, le=100, description="Score minimum")
    max_score: float = Field(ge=0, le=100, description="Score maximum")
    nb_assessments: int = Field(ge=0, description="Nombre d'évaluations")
    date_registration: int = Field(description="Date d'inscription (relative)")
    is_unregistered: int = Field(ge=0, le=1, description="Désinscription (0/1)")
    
    class Config:
        json_schema_extra = {
            "example": {
                "num_of_prev_attempts": 0,
                "studied_credits": 60,
                "total_clicks": 1500,
                "nb_sessions": 300,
                "avg_clicks": 5.0,
                "max_clicks": 50,
                "avg_score": 75.0,
                "min_score": 60.0,
                "max_score": 90.0,
                "nb_assessments": 5,
                "date_registration": -50,
                "is_unregistered": 0
            }
        }


class PredictionResponse(BaseModel):
    """Réponse de prédiction"""
    student_id: Optional[str] = Field(None, description="ID de l'étudiant")
    prediction: int = Field(description="Prédiction (0=Échec, 1=Succès)")
    prediction_label: str = Field(description="Label de la prédiction")
    probability: dict = Field(description="Probabilités par classe")
    confidence: float = Field(ge=0, le=1, description="Confiance de la prédiction")
    risk_level: str = Field(description="Niveau de risque (low/medium/high)")
    recommendations: Optional[List[str]] = Field(None, description="Recommandations")
    
    class Config:
        json_schema_extra = {
            "example": {
                "student_id": "12345",
                "prediction": 1,
                "prediction_label": "Succès",
                "probability": {
                    "echec": 0.23,
                    "succes": 0.77
                },
                "confidence": 0.77,
                "risk_level": "low",
                "recommendations": [
                    "Continuez votre excellent travail",
                    "Maintenez votre rythme d'étude"
                ]
            }
        }


class BatchPredictionRequest(BaseModel):
    """Requête de prédictions en batch"""
    students: List[PredictionRequest] = Field(description="Liste des étudiants")
    
    class Config:
        json_schema_extra = {
            "example": {
                "students": [
                    {
                        "num_of_prev_attempts": 0,
                        "studied_credits": 60,
                        "total_clicks": 1500,
                        "nb_sessions": 300,
                        "avg_clicks": 5.0,
                        "max_clicks": 50,
                        "avg_score": 75.0,
                        "min_score": 60.0,
                        "max_score": 90.0,
                        "nb_assessments": 5,
                        "date_registration": -50,
                        "is_unregistered": 0
                    }
                ]
            }
        }


class BatchPredictionResponse(BaseModel):
    """Réponse de prédictions en batch"""
    count: int = Field(description="Nombre de prédictions")
    results: List[PredictionResponse] = Field(description="Résultats des prédictions")
    summary: dict = Field(description="Résumé des prédictions")
    
    class Config:
        json_schema_extra = {
            "example": {
                "count": 2,
                "results": [
                    {
                        "prediction": 1,
                        "prediction_label": "Succès",
                        "probability": {"echec": 0.23, "succes": 0.77},
                        "confidence": 0.77,
                        "risk_level": "low"
                    }
                ],
                "summary": {
                    "success_count": 1,
                    "failure_count": 1,
                    "average_confidence": 0.75
                }
            }
        }
