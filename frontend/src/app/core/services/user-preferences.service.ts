import { Injectable } from '@angular/core';

export interface UserPreferences {
  itemsPerPage?: number;
  sortField?: string;
  sortDirection?: 'asc' | 'desc';
  selectedStatus?: string;
  selectedEnglishLevel?: string;
  selectedPaymentStatus?: string;
  selectedExperienceRange?: string;
  selectedCity?: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserPreferencesService {
  private readonly STORAGE_KEY_PREFIX = 'user_prefs_';

  constructor() {}

  savePreferences(page: string, preferences: UserPreferences): void {
    try {
      const key = this.STORAGE_KEY_PREFIX + page;
      localStorage.setItem(key, JSON.stringify(preferences));
    } catch (error) {
      console.error('Error saving preferences:', error);
    }
  }

  loadPreferences(page: string): UserPreferences | null {
    try {
      const key = this.STORAGE_KEY_PREFIX + page;
      const data = localStorage.getItem(key);
      return data ? JSON.parse(data) : null;
    } catch (error) {
      console.error('Error loading preferences:', error);
      return null;
    }
  }

  clearPreferences(page: string): void {
    try {
      const key = this.STORAGE_KEY_PREFIX + page;
      localStorage.removeItem(key);
    } catch (error) {
      console.error('Error clearing preferences:', error);
    }
  }

  clearAllPreferences(): void {
    try {
      const keys = Object.keys(localStorage);
      keys.forEach(key => {
        if (key.startsWith(this.STORAGE_KEY_PREFIX)) {
          localStorage.removeItem(key);
        }
      });
    } catch (error) {
      console.error('Error clearing all preferences:', error);
    }
  }
}
