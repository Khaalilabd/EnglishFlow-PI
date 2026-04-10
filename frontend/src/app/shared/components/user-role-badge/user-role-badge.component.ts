import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface RoleBadgeConfig {
  label: string;
  icon: string;
  bgColor: string;
  textColor: string;
  borderColor: string;
}

@Component({
  selector: 'app-user-role-badge',
  standalone: true,
  imports: [CommonModule],
  template: `
    <span 
      *ngIf="badgeConfig"
      class="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold border transition-all duration-200 hover:scale-105"
      [ngStyle]="{
        'background-color': badgeConfig.bgColor,
        'color': badgeConfig.textColor,
        'border-color': badgeConfig.borderColor
      }">
      <span class="text-sm">{{ badgeConfig.icon }}</span>
      <span>{{ badgeConfig.label }}</span>
    </span>
  `,
  styles: [`
    :host {
      display: inline-block;
    }
  `]
})
export class UserRoleBadgeComponent {
  @Input() role: string = '';
  @Input() size: 'sm' | 'md' | 'lg' = 'md';

  private roleBadges: { [key: string]: RoleBadgeConfig } = {
    'ADMIN': {
      label: 'Admin',
      icon: '👑',
      bgColor: '#FEF3C7',
      textColor: '#92400E',
      borderColor: '#FCD34D'
    },
    'TUTOR': {
      label: 'Tutor',
      icon: '🎓',
      bgColor: '#DBEAFE',
      textColor: '#1E40AF',
      borderColor: '#93C5FD'
    },
    'ACADEMIC_OFFICE_AFFAIR': {
      label: 'Academic Staff',
      icon: '📋',
      bgColor: '#D1FAE5',
      textColor: '#065F46',
      borderColor: '#6EE7B7'
    },
    'STUDENT': {
      label: 'Student',
      icon: '🎒',
      bgColor: '#F3F4F6',
      textColor: '#374151',
      borderColor: '#D1D5DB'
    }
  };

  get badgeConfig(): RoleBadgeConfig | null {
    return this.roleBadges[this.role] || null;
  }
}
