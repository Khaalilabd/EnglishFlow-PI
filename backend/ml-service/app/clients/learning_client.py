"""
Client HTTP pour le Learning Service
"""
import httpx
import logging
from typing import Optional, Dict, Any, List
from ..config import settings

logger = logging.getLogger(__name__)


class LearningClient:
    """Client pour communiquer avec le Learning Service"""
    
    def __init__(self):
        self.base_url = settings.learning_service_url
        self.timeout = 10.0
    
    async def get_quiz_results(self, student_id: str) -> List[Dict[str, Any]]:
        """Récupère les résultats des quiz d'un étudiant"""
        try:
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.get(
                    f"{self.base_url}/api/quizzes/student/{student_id}/results"
                )
                
                if response.status_code == 200:
                    return response.json()
                else:
                    logger.warning(f"Résultats quiz non trouvés pour {student_id}")
                    return []
        
        except Exception as e:
            logger.error(f"Erreur lors de la récupération des résultats quiz: {e}")
            return []
    
    async def get_reading_progress(self, student_id: str) -> List[Dict[str, Any]]:
        """Récupère la progression de lecture d'un étudiant"""
        try:
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.get(
                    f"{self.base_url}/api/ebooks/student/{student_id}/progress"
                )
                
                if response.status_code == 200:
                    return response.json()
                else:
                    return []
        
        except Exception as e:
            logger.error(f"Erreur lors de la récupération de la progression lecture: {e}")
            return []
    
    async def get_student_statistics(self, student_id: str) -> Optional[Dict[str, Any]]:
        """Récupère les statistiques d'apprentissage d'un étudiant"""
        try:
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.get(
                    f"{self.base_url}/api/statistics/student/{student_id}"
                )
                
                if response.status_code == 200:
                    return response.json()
                else:
                    return None
        
        except Exception as e:
            logger.error(f"Erreur lors de la récupération des statistiques: {e}")
            return None
    
    async def health_check(self) -> bool:
        """Vérifie que le Learning Service est accessible"""
        try:
            async with httpx.AsyncClient(timeout=5.0) as client:
                response = await client.get(f"{self.base_url}/actuator/health")
                return response.status_code == 200
        except:
            return False
