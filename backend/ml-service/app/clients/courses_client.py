"""
Client HTTP pour le Courses Service
"""
import httpx
import logging
from typing import Optional, Dict, Any, List
from ..config import settings

logger = logging.getLogger(__name__)


class CoursesClient:
    """Client pour communiquer avec le Courses Service"""
    
    def __init__(self):
        self.base_url = settings.courses_service_url
        self.timeout = 10.0
    
    async def get_student_enrollments(self, student_id: str) -> List[Dict[str, Any]]:
        """Récupère l'historique d'inscription d'un étudiant"""
        try:
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.get(
                    f"{self.base_url}/api/enrollments/student/{student_id}"
                )
                
                if response.status_code == 200:
                    return response.json()
                else:
                    logger.warning(f"Inscriptions non trouvées pour {student_id}")
                    return []
        
        except Exception as e:
            logger.error(f"Erreur lors de la récupération des inscriptions: {e}")
            return []
    
    async def get_course_details(self, course_id: str) -> Optional[Dict[str, Any]]:
        """Récupère les détails d'un cours"""
        try:
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.get(f"{self.base_url}/api/courses/{course_id}")
                
                if response.status_code == 200:
                    return response.json()
                else:
                    return None
        
        except Exception as e:
            logger.error(f"Erreur lors de la récupération du cours {course_id}: {e}")
            return None
    
    async def get_student_progress(self, student_id: str, course_id: str) -> Optional[Dict[str, Any]]:
        """Récupère la progression d'un étudiant dans un cours"""
        try:
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.get(
                    f"{self.base_url}/api/progress/student/{student_id}/course/{course_id}"
                )
                
                if response.status_code == 200:
                    return response.json()
                else:
                    return None
        
        except Exception as e:
            logger.error(f"Erreur lors de la récupération de la progression: {e}")
            return None
    
    async def health_check(self) -> bool:
        """Vérifie que le Courses Service est accessible"""
        try:
            async with httpx.AsyncClient(timeout=5.0) as client:
                response = await client.get(f"{self.base_url}/actuator/health")
                return response.status_code == 200
        except:
            return False
