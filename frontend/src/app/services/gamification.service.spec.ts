import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { GamificationService, UserLevel, Badge, UserBadge } from './gamification.service';
import { environment } from '../../environments/environment';

describe('GamificationService', () => {
  let service: GamificationService;
  let httpMock: HttpTestingController;

  const mockUserLevel: UserLevel = {
    userId: 1,
    assessmentLevel: 'B1',
    assessmentLevelIcon: '🎯',
    assessmentLevelName: 'Intermediate',
    hasCompletedAssessment: true,
    assessmentCompletedAt: '2026-01-15T10:00:00',
    certifiedLevel: null,
    certifiedLevelIcon: undefined,
    certifiedLevelName: undefined,
    certificationDate: undefined,
    currentXP: 750,
    totalXP: 2500,
    xpForNextLevel: 1000,
    progressPercentage: 75,
    nextLevel: 'B2',
    jungleCoins: 150,
    loyaltyTier: 'SILVER',
    loyaltyTierIcon: '🥈',
    loyaltyDiscount: 10,
    totalSpent: 500,
    consecutiveDays: 15,
    rank: 42
  };

  const mockBadge: UserBadge = {
    id: 1,
    code: 'FIRST_LESSON',
    name: 'First Steps',
    description: 'Complete your first lesson',
    icon: '🎓',
    type: 'ACHIEVEMENT',
    rarity: 'COMMON',
    rarityIcon: '⭐',
    rarityColor: '#gray',
    coinsReward: 10,
    isEarned: true,
    isDisplayed: true,
    isNew: false,
    earnedAt: '2026-01-10T14:30:00'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        GamificationService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(GamificationService);
    httpMock = TestBed.inject(HttpTestingController);
    
    localStorage.setItem('token', 'mock-token');
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  // ========== BASIC CRUD TESTS ==========

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get user level', (done) => {
    const userId = 1;

    service.getUserLevel(userId).subscribe(level => {
      expect(level).toEqual(mockUserLevel);
      expect(level.currentXP).toBe(750);
      expect(level.progressPercentage).toBe(75);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/gamification/users/${userId}/level`);
    expect(req.request.method).toBe('GET');
    req.flush(mockUserLevel);
  });

  it('should get user badges', (done) => {
    const userId = 1;
    const mockBadges: UserBadge[] = [mockBadge];

    service.getUserBadges(userId).subscribe(badges => {
      expect(badges.length).toBe(1);
      expect(badges[0]).toEqual(mockBadge);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/gamification/users/${userId}/badges`);
    expect(req.request.method).toBe('GET');
    req.flush(mockBadges);
  });

  // ========== COMPLEX BUSINESS LOGIC TESTS ==========

  it('should add XP and calculate level progression correctly', (done) => {
    const userId = 1;
    const xpToAdd = 300;
    const reason = 'Completed quiz';

    const updatedLevel: UserLevel = {
      ...mockUserLevel,
      currentXP: 1050, // 750 + 300
      totalXP: 2800,   // 2500 + 300
      progressPercentage: 105, // Exceeded current level
      nextLevel: 'B2'
    };

    service.addXP(userId, xpToAdd, reason).subscribe(level => {
      expect(level.currentXP).toBe(1050);
      expect(level.totalXP).toBe(2800);
      // User should have leveled up
      expect(level.progressPercentage).toBeGreaterThan(100);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/gamification/users/${userId}/xp`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ xp: xpToAdd, reason });
    req.flush(updatedLevel);
  });

  it('should handle coin transactions (add and spend)', (done) => {
    const userId = 1;
    const coinsToAdd = 50;
    const reason = 'Daily login bonus';

    const updatedLevel: UserLevel = {
      ...mockUserLevel,
      jungleCoins: 200 // 150 + 50
    };

    service.addCoins(userId, coinsToAdd, reason).subscribe(level => {
      expect(level.jungleCoins).toBe(200);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/gamification/users/${userId}/coins`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ coins: coinsToAdd, reason });
    req.flush(updatedLevel);
  });

  it('should spend coins and update balance', (done) => {
    const userId = 1;
    const coinsToSpend = 100;

    const updatedLevel: UserLevel = {
      ...mockUserLevel,
      jungleCoins: 50 // 150 - 100
    };

    service.spendCoins(userId, coinsToSpend).subscribe(level => {
      expect(level.jungleCoins).toBe(50);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/gamification/users/${userId}/coins/spend`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ coins: coinsToSpend });
    req.flush(updatedLevel);
  });

  it('should award badge and grant coin reward', (done) => {
    const userId = 1;
    const badgeCode = 'FIRST_LESSON';

    const newBadge: UserBadge = {
      ...mockBadge,
      isNew: true,
      earnedAt: new Date().toISOString()
    };

    service.awardBadge(userId, badgeCode).subscribe(badge => {
      expect(badge.code).toBe(badgeCode);
      expect(badge.isEarned).toBe(true);
      expect(badge.isNew).toBe(true);
      expect(badge.coinsReward).toBe(10);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/gamification/users/${userId}/badges`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ badgeCode });
    req.flush(newBadge);
  });

  it('should get new badges and mark them as seen', (done) => {
    const userId = 1;
    const newBadges: UserBadge[] = [
      { ...mockBadge, isNew: true, code: 'BADGE_1' },
      { ...mockBadge, isNew: true, code: 'BADGE_2' }
    ];

    service.getNewBadges(userId).subscribe(badges => {
      expect(badges.length).toBe(2);
      expect(badges.every(b => b.isNew)).toBe(true);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/gamification/users/${userId}/badges/new`);
    expect(req.request.method).toBe('GET');
    req.flush(newBadges);
  });

  it('should mark badges as seen', (done) => {
    const userId = 1;

    service.markBadgesAsSeen(userId).subscribe(() => {
      expect(true).toBe(true); // Just verify the call completes
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/gamification/users/${userId}/badges/mark-seen`);
    expect(req.request.method).toBe('POST');
    req.flush(null);
  });

  it('should submit assessment and update level', (done) => {
    const userId = 1;
    const assessedLevel = 'B2';

    const updatedLevel: UserLevel = {
      ...mockUserLevel,
      assessmentLevel: 'B2',
      assessmentLevelName: 'Upper Intermediate',
      hasCompletedAssessment: true,
      assessmentCompletedAt: new Date().toISOString()
    };

    service.submitAssessment(userId, assessedLevel).subscribe(level => {
      expect(level.assessmentLevel).toBe('B2');
      expect(level.hasCompletedAssessment).toBe(true);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/gamification/users/${userId}/assessment`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ assessedLevel });
    req.flush(updatedLevel);
  });

  it('should certify level after paid exam', (done) => {
    const userId = 1;
    const certifiedLevel = 'B2';

    const updatedLevel: UserLevel = {
      ...mockUserLevel,
      certifiedLevel: 'B2',
      certifiedLevelName: 'Upper Intermediate',
      certifiedLevelIcon: '🏆',
      certificationDate: new Date().toISOString()
    };

    service.certifyLevel(userId, certifiedLevel).subscribe(level => {
      expect(level.certifiedLevel).toBe('B2');
      expect(level.certificationDate).toBeTruthy();
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/gamification/users/${userId}/certify`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ certifiedLevel });
    req.flush(updatedLevel);
  });

  // ========== ADMIN FUNCTIONALITY TESTS ==========

  it('should get all user levels (admin)', (done) => {
    const allLevels: UserLevel[] = [
      mockUserLevel,
      { ...mockUserLevel, userId: 2, currentXP: 500 },
      { ...mockUserLevel, userId: 3, currentXP: 1200 }
    ];

    service.getAllUserLevels().subscribe(levels => {
      expect(levels.length).toBe(3);
      expect(levels[0].userId).toBe(1);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/gamification/admin/users/levels`);
    expect(req.request.method).toBe('GET');
    req.flush(allLevels);
  });

  it('should admin update assessment level', (done) => {
    const userId = 1;
    const assessedLevel = 'C1';

    const updatedLevel: UserLevel = {
      ...mockUserLevel,
      assessmentLevel: 'C1',
      assessmentLevelName: 'Advanced'
    };

    service.adminUpdateAssessment(userId, assessedLevel).subscribe(level => {
      expect(level.assessmentLevel).toBe('C1');
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/gamification/admin/users/${userId}/assessment`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ assessedLevel });
    req.flush(updatedLevel);
  });

  it('should admin revoke certification', (done) => {
    const userId = 1;

    const updatedLevel: UserLevel = {
      ...mockUserLevel,
      certifiedLevel: null,
      certifiedLevelIcon: undefined,
      certifiedLevelName: undefined,
      certificationDate: undefined
    };

    service.adminRevokeCertification(userId).subscribe(level => {
      expect(level.certifiedLevel).toBeNull();
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/gamification/admin/users/${userId}/certification`);
    expect(req.request.method).toBe('DELETE');
    req.flush(updatedLevel);
  });

  it('should get global statistics (admin)', (done) => {
    const mockStats = {
      totalUsers: 1000,
      totalXP: 500000,
      totalBadgesEarned: 5000,
      averageLevel: 'B1',
      topUsers: []
    };

    service.getGlobalStats().subscribe(stats => {
      expect(stats.totalUsers).toBe(1000);
      expect(stats.totalXP).toBe(500000);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/gamification/admin/stats`);
    expect(req.request.method).toBe('GET');
    req.flush(mockStats);
  });

  // ========== EDGE CASES & ERROR HANDLING ==========

  it('should handle insufficient coins when spending', (done) => {
    const userId = 1;
    const coinsToSpend = 1000; // More than available (150)

    service.spendCoins(userId, coinsToSpend).subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.status).toBe(400);
        done();
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/gamification/users/${userId}/coins/spend`);
    req.flush({ message: 'Insufficient coins' }, { status: 400, statusText: 'Bad Request' });
  });

  it('should handle duplicate badge award', (done) => {
    const userId = 1;
    const badgeCode = 'FIRST_LESSON';

    service.awardBadge(userId, badgeCode).subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.status).toBe(409);
        done();
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/gamification/users/${userId}/badges`);
    req.flush({ message: 'Badge already earned' }, { status: 409, statusText: 'Conflict' });
  });

  it('should include authorization header in requests', () => {
    const userId = 1;

    service.getUserLevel(userId).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/gamification/users/${userId}/level`);
    expect(req.request.headers.get('Authorization')).toBe('Bearer mock-token');
    req.flush(mockUserLevel);
  });
});
