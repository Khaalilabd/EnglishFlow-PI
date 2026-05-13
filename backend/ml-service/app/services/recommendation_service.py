"""
Service de recommandation de cours
"""
import logging
from typing import List, Optional
from ..ml_models import CourseRecommendationModel
from ..schemas.recommendation import RecommendationResponse, NewStudentRecommendationRequest

logger = logging.getLogger(__name__)


class RecommendationService:
    """Service de recommandation"""
    
    def __init__(self, model_path: str = "app/models/recommendation_model.pkl"):
        self.model = CourseRecommendationModel()
        self.model_loaded = False
        try:
            self.model.load(model_path)
            self.model_loaded = True
            logger.info(f"✅ Modèle de recommandation chargé depuis {model_path}")
        except Exception as e:
            logger.error(f"❌ Erreur lors du chargement du modèle: {e}")
            logger.warning("Le service de recommandation fonctionnera en mode dégradé")
    
    def recommend_for_student(
        self, 
        student_id: str, 
        n_recommendations: int = 5,
        min_students: int = 5
    ) -> List[RecommendationResponse]:
        """Recommande des cours pour un étudiant existant"""
        if not self.model_loaded:
            logger.warning("Modèle non chargé, retour de recommandations par défaut")
            return self._get_default_recommendations(n_recommendations)
        
        try:
            # Obtenir les recommandations du modèle
            recommendations = self.model.recommend_for_student(
                student_id=student_id,
                n_recommendations=n_recommendations,
                min_students=min_students
            )
            
            # Convertir en RecommendationResponse
            return [
                RecommendationResponse(
                    course_code=rec['code_module'],
                    course_name=self._get_course_name(rec['code_module']),
                    recommendation_score=rec['recommendation_score'],
                    avg_interaction=rec['avg_interaction'],
                    nb_students=rec['nb_students'],
                    success_rate=rec['success_rate'],
                    reason=self._generate_reason(rec)
                )
                for rec in recommendations
            ]
        
        except Exception as e:
            logger.error(f"Erreur lors de la recommandation: {e}")
            return self._get_default_recommendations(n_recommendations)
    
    def recommend_for_new_student(
        self, 
        request: NewStudentRecommendationRequest
    ) -> List[RecommendationResponse]:
        """Recommande des cours pour un nouvel étudiant"""
        if not self.model_loaded:
            return self._get_default_recommendations(request.n_recommendations)
        
        try:
            student_profile = {
                'avg_score': request.avg_score,
                'studied_credits': request.studied_credits or 0,
                'english_level': request.english_level or 'B1'
            }
            
            recommendations = self.model.recommend_for_new_student(
                student_profile=student_profile,
                n_recommendations=request.n_recommendations
            )
            
            return [
                RecommendationResponse(
                    course_code=rec['code_module'],
                    course_name=self._get_course_name(rec['code_module']),
                    recommendation_score=rec['recommendation_score'],
                    avg_interaction=rec['avg_interaction'],
                    nb_students=rec['nb_students'],
                    success_rate=rec['success_rate'],
                    reason=rec.get('reason', 'Cours recommandé pour votre profil')
                )
                for rec in recommendations
            ]
        
        except Exception as e:
            logger.error(f"Erreur lors de la recommandation: {e}")
            return self._get_default_recommendations(request.n_recommendations)
    
    def get_course_details(self, course_code: str) -> Optional[dict]:
        """Retourne les détails d'un cours"""
        if not self.model_loaded:
            return None
        
        try:
            return self.model.get_course_details(course_code)
        except Exception as e:
            logger.error(f"Erreur lors de la récupération des détails: {e}")
            return None
    
    def _get_course_name(self, course_code: str) -> str:
        """Retourne le nom complet d'un cours"""
        # Mapping des codes de cours (à adapter selon vos données)
        course_names = {
            'AAA': 'Advanced Analytics',
            'BBB': 'Business Basics',
            'CCC': 'Creative Computing',
            'DDD': 'Data Design',
            'EEE': 'English Essentials',
            'FFF': 'Financial Fundamentals',
            'GGG': 'Global Geography'
        }
        return course_names.get(course_code, course_code)
    
    def _generate_reason(self, recommendation: dict) -> str:
        """Génère une raison pour la recommandation"""
        success_rate = recommendation['success_rate']
        nb_students = recommendation['nb_students']
        
        if success_rate >= 0.75:
            return f"Excellent taux de réussite ({success_rate:.0%}) - Cours très apprécié par {nb_students} étudiants"
        elif success_rate >= 0.60:
            return f"Bon équilibre difficulté/réussite - Suivi par {nb_students} étudiants"
        else:
            return f"Cours avec bon support pédagogique - {nb_students} étudiants inscrits"
    
    def _get_default_recommendations(self, n: int) -> List[RecommendationResponse]:
        """Retourne des recommandations par défaut"""
        default_courses = [
            {
                'course_code': 'AAA',
                'course_name': 'Advanced Analytics',
                'recommendation_score': 0.85,
                'avg_interaction': 4.2,
                'nb_students': 1500,
                'success_rate': 0.75,
                'reason': 'Cours populaire et bien noté'
            },
            {
                'course_code': 'BBB',
                'course_name': 'Business Basics',
                'recommendation_score': 0.80,
                'avg_interaction': 4.0,
                'nb_students': 2000,
                'success_rate': 0.80,
                'reason': 'Excellent taux de réussite'
            },
            {
                'course_code': 'CCC',
                'course_name': 'Creative Computing',
                'recommendation_score': 0.75,
                'avg_interaction': 3.8,
                'nb_students': 1200,
                'success_rate': 0.70,
                'reason': 'Cours innovant et engageant'
            },
            {
                'course_code': 'DDD',
                'course_name': 'Data Design',
                'recommendation_score': 0.70,
                'avg_interaction': 3.9,
                'nb_students': 1800,
                'success_rate': 0.72,
                'reason': 'Compétences très demandées'
            },
            {
                'course_code': 'EEE',
                'course_name': 'English Essentials',
                'recommendation_score': 0.65,
                'avg_interaction': 4.1,
                'nb_students': 2500,
                'success_rate': 0.85,
                'reason': 'Fondamentaux essentiels'
            }
        ]
        
        return [
            RecommendationResponse(**course)
            for course in default_courses[:n]
        ]
