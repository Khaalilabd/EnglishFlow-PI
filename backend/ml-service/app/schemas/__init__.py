"""
Schémas Pydantic pour la validation des données
"""
from .prediction import PredictionRequest, PredictionResponse, BatchPredictionRequest, BatchPredictionResponse
from .clustering import ClusteringRequest, ClusteringResponse, ClusterAnalysisResponse
from .recommendation import RecommendationResponse, NewStudentRecommendationRequest

__all__ = [
    "PredictionRequest",
    "PredictionResponse",
    "BatchPredictionRequest",
    "BatchPredictionResponse",
    "ClusteringRequest",
    "ClusteringResponse",
    "ClusterAnalysisResponse",
    "RecommendationResponse",
    "NewStudentRecommendationRequest",
]
