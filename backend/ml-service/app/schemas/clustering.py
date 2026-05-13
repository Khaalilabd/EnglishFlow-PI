"""
Schémas pour le clustering
"""
from pydantic import BaseModel, Field
from typing import List, Optional


class ClusteringRequest(BaseModel):
    """Requête de clustering pour un étudiant"""
    num_of_prev_attempts: int = Field(ge=0)
    studied_credits: int = Field(ge=0)
    total_clicks: int = Field(ge=0)
    nb_sessions: int = Field(ge=0)
    avg_clicks: float = Field(ge=0)
    max_clicks: int = Field(ge=0)
    avg_score: float = Field(ge=0, le=100)
    min_score: float = Field(ge=0, le=100)
    max_score: float = Field(ge=0, le=100)
    nb_assessments: int = Field(ge=0)
    nb_tma: int = Field(ge=0, description="Nombre de TMA")
    nb_cma: int = Field(ge=0, description="Nombre de CMA")
    nb_exams: int = Field(ge=0, description="Nombre d'examens")
    date_registration: int
    is_unregistered: int = Field(ge=0, le=1)
    module_presentation_length: int = Field(ge=0)
    
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
                "nb_tma": 3,
                "nb_cma": 2,
                "nb_exams": 1,
                "date_registration": -50,
                "is_unregistered": 0,
                "module_presentation_length": 180
            }
        }


class ClusteringResponse(BaseModel):
    """Réponse de clustering"""
    student_id: Optional[str] = None
    cluster: int = Field(description="ID du cluster")
    cluster_label: str = Field(description="Label du cluster")
    characteristics: dict = Field(description="Caractéristiques du cluster")
    recommendations: List[str] = Field(description="Recommandations basées sur le cluster")
    
    class Config:
        json_schema_extra = {
            "example": {
                "student_id": "12345",
                "cluster": 0,
                "cluster_label": "Étudiants Performants",
                "characteristics": {
                    "avg_clicks": 2000,
                    "avg_score": 80,
                    "success_rate": 0.85
                },
                "recommendations": [
                    "Groupe d'étudiants très engagés",
                    "Taux de réussite élevé"
                ]
            }
        }


class ClusterAnalysisResponse(BaseModel):
    """Analyse complète des clusters"""
    n_clusters: int = Field(description="Nombre de clusters")
    silhouette_score: float = Field(description="Score de silhouette")
    clusters: List[dict] = Field(description="Détails de chaque cluster")
    
    class Config:
        json_schema_extra = {
            "example": {
                "n_clusters": 3,
                "silhouette_score": 0.45,
                "clusters": [
                    {
                        "cluster_id": 0,
                        "label": "Performants",
                        "size": 5000,
                        "characteristics": {
                            "avg_clicks": 2000,
                            "avg_score": 80
                        }
                    }
                ]
            }
        }
