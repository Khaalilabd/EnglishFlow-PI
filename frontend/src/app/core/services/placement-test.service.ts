import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PlacementTestService {
  private showTestSubject = new BehaviorSubject<boolean>(false);
  public showTest$ = this.showTestSubject.asObservable();

  constructor() {}

  /**
   * Determines if user should see the placement test
   * Returns TRUE if user is a STUDENT and has NO valid English level
   * Returns FALSE if user has a valid English level or is not a student
   */
  shouldShowTest(user: any): boolean {
    // Check if user has a valid englishLevel (exists, is string, not empty/whitespace)
    const hasValidEnglishLevel = user?.englishLevel && 
                                 typeof user.englishLevel === 'string' && 
                                 user.englishLevel.trim() !== '';
    
    // User needs test if they are a STUDENT and DON'T have a valid English level
    const needsTest = user && 
                      user.role === 'STUDENT' && 
                      !hasValidEnglishLevel;
    
    console.log('🔍 Placement Test Check:', {
      userId: user?.id,
      role: user?.role,
      englishLevel: user?.englishLevel,
      hasValidEnglishLevel: hasValidEnglishLevel,
      needsTest: needsTest
    });
    
    return needsTest;
  }

  triggerTest(): void {
    this.showTestSubject.next(true);
  }

  hideTest(): void {
    this.showTestSubject.next(false);
  }

  get isTestVisible(): boolean {
    return this.showTestSubject.value;
  }
}
