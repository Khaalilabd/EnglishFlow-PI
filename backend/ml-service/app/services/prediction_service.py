"""
Service de prédiction du succès étudiant
"""
import pandas as pd
import logging
from typing import List, Dict, Any
from ..ml_models import StudentPredictionModel
from ..schemas.prediction import PredictionRequest, PredictionResponse

logger = logging.getLogger(__name__)


class PredictionService:
    """Service de prédiction"""
    
    def __init__(self, model_path: str = "app/models/best_prediction_model.pkl"):
        self.model = StudentPredictionModel()
        self.model_loaded = False
        try:
            self.model.load(model_path)
            self.model_loaded = True
            logger.info(f"✅ Modèle de prédiction chargé depuis {model_path}")
        except Exception as e:
            logger.error(f"❌ Erreur lors du chargement du modèle: {e}")
    
    def predict_single(self, data: PredictionRequest, student_id: str = None) -> PredictionResponse:
        """Prédit le succès d'un étudiant"""
        if not self.model_loaded:
            raise ValueError("Modèle de prédiction non chargé")
        
        # Préparer les données
        features = [
            'num_of_prev_attempts', 'studied_credits',
            'total_clicks', 'nb_sessions', 'avg_clicks', 'max_clicks',
            'avg_score', 'min_score', 'max_score', 'nb_assessments',
            'date_registration', 'is_unregistered'
        ]
        
        X = pd.DataFrame([{f: getattr(data, f) for f in features}])
        
        # Prédiction
        prediction = int(self.model.predict(X)[0])
        proba = self.model.predict_proba(X)[0]
        
        # Déterminer le niveau de risque
        risk_level = self._calculate_risk_level(proba[1])
        
        # Générer des recommandations
        recommendations = self._generate_recommendations(prediction, proba[1], data)
        
        return PredictionResponse(
            student_id=student_id,
            prediction=prediction,
            prediction_label="Success" if prediction == 1 else "Failure",
            probability={
                "echec": float(proba[0]),
                "succes": float(proba[1])
            },
            confidence=float(max(proba)),
            risk_level=risk_level,
            recommendations=recommendations
        )
    
    def predict_batch(self, students: List[PredictionRequest]) -> Dict[str, Any]:
        """Prédit pour plusieurs étudiants"""
        if not self.model_loaded:
            raise ValueError("Modèle de prédiction non chargé")
        
        results = []
        success_count = 0
        failure_count = 0
        total_confidence = 0.0
        
        for i, student in enumerate(students):
            result = self.predict_single(student, student_id=str(i))
            results.append(result)
            
            if result.prediction == 1:
                success_count += 1
            else:
                failure_count += 1
            
            total_confidence += result.confidence
        
        return {
            "count": len(results),
            "results": results,
            "summary": {
                "success_count": success_count,
                "failure_count": failure_count,
                "success_rate": success_count / len(results) if results else 0,
                "average_confidence": total_confidence / len(results) if results else 0
            }
        }
    
    def _calculate_risk_level(self, success_probability: float) -> str:
        """Calcule le niveau de risque"""
        if success_probability >= 0.7:
            return "low"
        elif success_probability >= 0.5:
            return "medium"
        else:
            return "high"
    
    def _generate_recommendations(
        self, 
        prediction: int, 
        success_probability: float,
        data: PredictionRequest
    ) -> List[str]:
        """Generate personalized recommendations"""
        recommendations = []
        
        if prediction == 1:  # Success predicted
            if success_probability >= 0.8:
                recommendations.append("✅ Excellent chances of success! Keep up the great work.")
            else:
                recommendations.append("✅ Good chances of success. Maintain your study pace.")
            
            if data.avg_clicks < 3:
                recommendations.append("💡 Increase your engagement on the platform to consolidate your learning.")
        
        else:  # Failure predicted
            recommendations.append("⚠️ Risk of failure detected. Intervention is recommended.")
            
            if data.total_clicks < 1000:
                recommendations.append("📚 Increase your study time and engagement on the platform.")
            
            if data.avg_score < 60:
                recommendations.append("📖 Focus on improving your assessment scores.")
            
            if data.nb_sessions < 200:
                recommendations.append("⏰ Increase the frequency of your study sessions.")
            
            if data.num_of_prev_attempts > 0:
                recommendations.append("🎯 Consult a tutor to identify your difficulties.")
        
        return recommendations
    
    def get_feature_importance(self) -> Dict[str, float]:
        """Retourne l'importance des features"""
        if not self.model_loaded:
            return {}
        
        features = [
            'num_of_prev_attempts', 'studied_credits',
            'total_clicks', 'nb_sessions', 'avg_clicks', 'max_clicks',
            'avg_score', 'min_score', 'max_score', 'nb_assessments',
            'date_registration', 'is_unregistered'
        ]
        
        importance = self.model.get_feature_importance(features)
        return importance if importance else {}
