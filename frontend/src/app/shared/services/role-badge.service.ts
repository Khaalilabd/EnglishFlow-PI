import { Injectable } from '@angular/core';

export interface RoleBadgeConfig {
  label: string;
  icon: string;
  bgColor: string;
  textColor: string;
  borderColor: string;
  priority: number; // Pour gérer les utilisateurs avec plusieurs rôles
}

@Injectable({
  providedIn: 'root'
})
export class RoleBadgeService {
  
  private roleBadges: { [key: string]: RoleBadgeConfig } = {
    'ADMIN': {
      label: 'Admin',
      icon: '👑',
      bgColor: '#FEF3C7',
      textColor: '#92400E',
      borderColor: '#FCD34D',
      priority: 1
    },
    'TUTOR': {
      label: 'Tutor',
      icon: '🎓',
      bgColor: '#DBEAFE',
      textColor: '#1E40AF',
      borderColor: '#93C5FD',
      priority: 2
    },
    'ACADEMIC_OFFICE_AFFAIR': {
      label: 'Academic Staff',
      icon: '📋',
      bgColor: '#D1FAE5',
      textColor: '#065F46',
      borderColor: '#6EE7B7',
      priority: 3
    },
    'STUDENT': {
      label: 'Student',
      icon: '🎒',
      bgColor: '#F3F4F6',
      textColor: '#374151',
      borderColor: '#D1D5DB',
      priority: 4
    }
  };

  /**
   * Obtenir la configuration du badge pour un rôle
   */
  getBadgeConfig(role: string): RoleBadgeConfig | null {
    return this.roleBadges[role] || null;
  }

  /**
   * Obtenir tous les badges configurés
   */
  getAllBadges(): { [key: string]: RoleBadgeConfig } {
    return this.roleBadges;
  }

  /**
   * Vérifier si un rôle a un badge
   */
  hasBadge(role: string): boolean {
    return !!this.roleBadges[role];
  }

  /**
   * Obtenir le badge avec la plus haute priorité (pour utilisateurs multi-rôles)
   */
  getHighestPriorityBadge(roles: string[]): RoleBadgeConfig | null {
    const badges = roles
      .map(role => this.getBadgeConfig(role))
      .filter(badge => badge !== null) as RoleBadgeConfig[];
    
    if (badges.length === 0) return null;
    
    return badges.reduce((highest, current) => 
      current.priority < highest.priority ? current : highest
    );
  }
}
