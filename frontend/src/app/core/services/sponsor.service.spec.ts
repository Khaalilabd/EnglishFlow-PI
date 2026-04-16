import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { SponsorService } from './sponsor.service';
import { Sponsor, SponsorLevel } from '../models/sponsor.model';
import { environment } from '../../../environments/environment';

describe('SponsorService', () => {
  let service: SponsorService;
  let httpMock: HttpTestingController;

  const mockSponsor: Sponsor = {
    id: 1,
    name: 'Tech Corp',
    description: 'Leading technology company',
    logo: 'tech-corp-logo.png',
    website: 'https://techcorp.com',
    contactEmail: 'contact@techcorp.com',
    contactPhone: '+216 12 345 678',
    level: SponsorLevel.GOLD,
    contributionAmount: 5000,
    status: 'APPROVED',
    userId: 10,
    createdAt: '2026-01-15T10:00:00'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SponsorService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(SponsorService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all sponsors', (done) => {
    const mockSponsors: Sponsor[] = [mockSponsor];

    service.getAllSponsors().subscribe(sponsors => {
      expect(sponsors.length).toBe(1);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/sponsors/approved`);
    expect(req.request.method).toBe('GET');
    req.flush(mockSponsors);
  });

  it('should get sponsor by ID', (done) => {
    const sponsorId = 1;

    service.getSponsorById(sponsorId).subscribe(sponsor => {
      expect(sponsor).toEqual(mockSponsor);
      expect(sponsor.id).toBe(sponsorId);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/sponsors/${sponsorId}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockSponsor);
  });

  it('should create a new sponsor', (done) => {
    const newSponsor = {
      name: 'New Sponsor',
      description: 'New sponsor description',
      contributionAmount: 3000
    };

    service.createSponsor(newSponsor).subscribe(sponsor => {
      expect(sponsor).toEqual(mockSponsor);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/sponsors`);
    expect(req.request.method).toBe('POST');
    req.flush(mockSponsor);
  });

  it('should update a sponsor', (done) => {
    const sponsorId = 1;
    const updateData = {
      name: 'Updated Sponsor',
      contributionAmount: 6000
    };

    service.updateSponsor(sponsorId, updateData).subscribe(sponsor => {
      expect(sponsor.name).toBe('Updated Sponsor');
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/sponsors/${sponsorId}`);
    expect(req.request.method).toBe('PUT');
    req.flush({ ...mockSponsor, ...updateData });
  });

  it('should delete a sponsor', (done) => {
    const sponsorId = 1;

    service.deleteSponsor(sponsorId).subscribe(() => {
      expect(true).toBe(true);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/sponsors/${sponsorId}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should get sponsors by level', (done) => {
    const level = SponsorLevel.GOLD;
    const goldSponsors: Sponsor[] = [mockSponsor];

    service.getSponsorsByLevel(level).subscribe(sponsors => {
      expect(sponsors.length).toBe(1);
      expect(sponsors[0].level).toBe(level);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/sponsors/level/${level}`);
    expect(req.request.method).toBe('GET');
    req.flush(goldSponsors);
  });

  it('should cache sponsors and return from cache on second call', fakeAsync(() => {
    const mockSponsors: Sponsor[] = [mockSponsor];

    // First call
    service.getAllSponsors().subscribe(sponsors => {
      expect(sponsors).toEqual(mockSponsors);
    });

    const req1 = httpMock.expectOne(`${environment.apiUrl}/sponsors/approved`);
    req1.flush(mockSponsors);
    tick();

    // Second call - should return from cache
    service.getAllSponsors().subscribe(sponsors => {
      expect(sponsors).toEqual(mockSponsors);
    });

    httpMock.expectNone(`${environment.apiUrl}/sponsors/approved`);
  }));

  it('should force refresh and bypass cache', fakeAsync(() => {
    const mockSponsors: Sponsor[] = [mockSponsor];

    // First call
    service.getAllSponsors().subscribe();
    const req1 = httpMock.expectOne(`${environment.apiUrl}/sponsors/approved`);
    req1.flush(mockSponsors);
    tick();

    // Force refresh
    service.getAllSponsors(true).subscribe(sponsors => {
      expect(sponsors).toEqual(mockSponsors);
    });

    const req2 = httpMock.expectOne(`${environment.apiUrl}/sponsors/approved`);
    req2.flush(mockSponsors);
  }));

  it('should invalidate cache after creating sponsor', fakeAsync(() => {
    const mockSponsors: Sponsor[] = [mockSponsor];
    const newSponsor = {
      name: 'New Sponsor',
      contributionAmount: 3000
    };

    // Populate cache
    service.getAllSponsors().subscribe();
    const req1 = httpMock.expectOne(`${environment.apiUrl}/sponsors/approved`);
    req1.flush(mockSponsors);
    tick();

    // Create sponsor
    service.createSponsor(newSponsor).subscribe();
    const req2 = httpMock.expectOne(`${environment.apiUrl}/sponsors`);
    req2.flush(mockSponsor);
    tick();

    // Next call should hit API again
    service.getAllSponsors().subscribe();
    const req3 = httpMock.expectOne(`${environment.apiUrl}/sponsors/approved`);
    req3.flush(mockSponsors);
  }));

  it('should handle sponsor not found error', (done) => {
    const sponsorId = 999;

    service.getSponsorById(sponsorId).subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.status).toBe(404);
        done();
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/sponsors/${sponsorId}`);
    req.flush({ message: 'Sponsor not found' }, { status: 404, statusText: 'Not Found' });
  });
});
