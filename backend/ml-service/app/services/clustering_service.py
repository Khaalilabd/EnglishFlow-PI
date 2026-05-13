"""
Service de clustering des étudiants
"""
import pandas as pd
import logging
from typing import Dict, Any
from ..ml_models import StudentClusteringModel
from ..schemas.clustering import ClusteringRequest, ClusteringResponse

logger = logging.getLogger(__name__)


class ClusteringService:
    """Service de clustering"""
    
    # Labels des clusters (à ajuster selon vos résultats)
    CLUSTER_LABELS = {
        0: "Étudiants Performants",
        1: "Étudiants Moyens",
        2: "Étudiants À Risque"
    }
    
    def __init__(self, model_path: str = "app/models/clustering_model.pkl"):
        self.model = StudentClusteringModel()
        self.model_loaded = False
        try:
            self.model.load(model_path)
            self.model_loaded = True
            logger.info(f"✅ Modèle de clustering chargé depuis {model_path}")
        except Exception as e:
            logger.error(f"❌ Erreur lors du chargement du modèle: {e}")
    
    def predict_cluster(self, data: ClusteringRequest, student_id: str = None) -> ClusteringResponse:
        """Identifie le cluster d'un étudiant"""
        if not self.model_loaded:
            raise ValueError("Modèle de clustering non chargé")
        
        # Préparer les données
        features = [
            'num_of_prev_attempts', 'studied_credits',
            'total_clicks', 'nb_sessions', 'avg_clicks', 'max_clicks',
            'avg_score', 'min_score', 'max_score', 'nb_assessments',
            'nb_tma', 'nb_cma', 'nb_exams',
            'date_registration', 'is_unregistered',
            'module_presentation_length'
        ]
        
        X = pd.DataFrame([{f: getattr(data, f) for f in features}])
        
        # Prédiction du cluster
        cluster_id = int(self.model.predict(X)[0])
        cluster_label = self.CLUSTER_LABELS.get(cluster_id, f"Cluster {cluster_id}")
        
        # Caractéristiques du cluster
        characteristics = self._get_cluster_characteristics(cluster_id)
        
        # Recommandations
        recommendations = self._generate_cluster_recommendations(cluster_id, data)
        
        return ClusteringResponse(
            student_id=student_id,
            cluster=cluster_id,
            cluster_label=cluster_label,
            characteristics=characteristics,
            recommendations=recommendations
        )
    
    def _get_cluster_characteristics(self, cluster_id: int) -> Dict[str, Any]:
        """Retourne les caractéristiques typiques d'un cluster"""
        # Ces valeurs sont des exemples - à ajuster selon vos données réelles
        characteristics_map = {
            0: {  # Performants
                "avg_clicks": 2000,
                "avg_score": 80,
                "avg_sessions": 400,
                "success_rate": 0.85,
                "engagement_level": "high"
            },
            1: {  # Moyens
                "avg_clicks": 1200,
                "avg_score": 65,
                "avg_sessions": 250,
                "success_rate": 0.60,
                "engagement_level": "medium"
            },
            2: {  # À risque
                "avg_clicks": 600,
                "avg_score": 50,
                "avg_sessions": 150,
                "success_rate": 0.35,
                "engagement_level": "low"
            }
        }
        
        return characteristics_map.get(cluster_id, {})
    
    def _generate_cluster_recommendations(
        self, 
        cluster_id: int, 
        data: ClusteringRequest
    ) -> list:
        """Génère des recommandations basées sur le cluster"""
        recommendations = []
        
        if cluster_id == 0:  # Performants
            recommendations.append("🌟 Vous faites partie des étudiants les plus performants")
            recommendations.append("💪 Continuez à maintenir ce niveau d'excellence")
            recommendations.append("🎯 Envisagez des cours avancés pour vous challenger")
        
        elif cluster_id == 1:  # Moyens
            recommendations.append("📈 Vous êtes dans la moyenne, avec un bon potentiel")
            recommendations.append("💡 Augmentez votre engagement pour rejoindre le groupe des performants")
            recommendations.append("📚 Concentrez-vous sur l'amélioration de vos scores")
        
        elif cluster_id == 2:  # À risque
            recommendations.append("⚠️ Vous faites partie du groupe nécessitant un accompagnement")
            recommendations.append("🆘 Contactez un tuteur pour un suivi personnalisé")
            recommendations.append("📖 Augmentez significativement votre temps d'étude")
            recommendations.append("👥 Rejoignez des groupes d'étude pour plus de soutien")
        
        return recommendations
    
    def get_cluster_analysis(self) -> Dict[str, Any]:
        """Retourne une analyse globale des clusters"""
        if not self.model_loaded:
            return {}
        
        n_clusters = len(self.CLUSTER_LABELS)
        
        clusters_info = []
        for cluster_id, label in self.CLUSTER_LABELS.items():
            characteristics = self._get_cluster_characteristics(cluster_id)
            clusters_info.append({
                "cluster_id": cluster_id,
                "label": label,
                "characteristics": characteristics
            })
        
        return {
            "n_clusters": n_clusters,
            "clusters": clusters_info
        }
