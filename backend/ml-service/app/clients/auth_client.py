"""
Client HTTP pour le Auth Service
"""
import httpx
import logging
from typing import Optional, Dict, Any
from ..config import settings

logger = logging.getLogger(__name__)


class AuthClient:
    """Client pour communiquer avec le Auth Service"""
    
    def __init__(self):
        self.base_url = settings.auth_service_url
        self.timeout = 10.0
    
    async def get_student_profile(self, student_id: str) -> Optional[Dict[str, Any]]:
        """Récupère le profil d'un étudiant"""
        try:
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.get(f"{self.base_url}/api/users/{student_id}")
                
                if response.status_code == 200:
                    return response.json()
                else:
                    logger.warning(f"Étudiant {student_id} non trouvé: {response.status_code}")
                    return None
        
        except Exception as e:
            logger.error(f"Erreur lors de la récupération du profil {student_id}: {e}")
            return None
    
    async def get_students_batch(self, student_ids: list) -> Dict[str, Any]:
        """Récupère plusieurs profils étudiants"""
        try:
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.post(
                    f"{self.base_url}/api/users/batch",
                    json={"ids": student_ids}
                )
                
                if response.status_code == 200:
                    return response.json()
                else:
                    logger.warning(f"Erreur batch: {response.status_code}")
                    return {}
        
        except Exception as e:
            logger.error(f"Erreur lors de la récupération batch: {e}")
            return {}
    
    async def health_check(self) -> bool:
        """Vérifie que le Auth Service est accessible"""
        try:
            async with httpx.AsyncClient(timeout=5.0) as client:
                response = await client.get(f"{self.base_url}/actuator/health")
                return response.status_code == 200
        except:
            return False
