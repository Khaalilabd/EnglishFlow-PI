"""
Clients HTTP pour communiquer avec les autres microservices
"""
from .auth_client import AuthClient
from .courses_client import CoursesClient
from .learning_client import LearningClient
from .gamification_client import GamificationClient

__all__ = [
    "AuthClient",
    "CoursesClient",
    "LearningClient",
    "GamificationClient",
]
