"""
Client HTTP pour le Gamification Service
"""
import httpx
import logging
from typing import Optional, Dict, Any
from ..config import settings

logger = logging.getLogger(__name__)


class GamificationClient:
    """Client pour communiquer avec le Gamification Service"""
    
    def __init__(self):
        self.base_url = settings.gamification_service_url
        self.timeout = 10.0
    
    async def get_student_points(self, student_id: str) -> Optional[Dict[str, Any]]:
        """Récupère les points d'un étudiant"""
        try:
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.get(
                    f"{self.base_url}/api/gamification/student/{student_id}/points"
                )
                
                if response.status_code == 200:
                    return response.json()
                else:
                    logger.warning(f"Points non trouvés pour {student_id}")
                    return None
        
        except Exception as e:
            logger.error(f"Erreur lors de la récupération des points: {e}")
            return None
    
    async def get_student_level(self, student_id: str) -> Optional[Dict[str, Any]]:
        """Récupère le niveau d'un étudiant"""
        try:
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.get(
                    f"{self.base_url}/api/gamification/student/{student_id}/level"
                )
                
                if response.status_code == 200:
                    return response.json()
                else:
                    return None
        
        except Exception as e:
            logger.error(f"Erreur lors de la récupération du niveau: {e}")
            return None
    
    async def get_student_badges(self, student_id: str) -> list:
        """Récupère les badges d'un étudiant"""
        try:
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.get(
                    f"{self.base_url}/api/gamification/student/{student_id}/badges"
                )
                
                if response.status_code == 200:
                    return response.json()
                else:
                    return []
        
        except Exception as e:
            logger.error(f"Erreur lors de la récupération des badges: {e}")
            return []
    
    async def health_check(self) -> bool:
        """Vérifie que le Gamification Service est accessible"""
        try:
            async with httpx.AsyncClient(timeout=5.0) as client:
                response = await client.get(f"{self.base_url}/actuator/health")
                return response.status_code == 200
        except:
            return False
