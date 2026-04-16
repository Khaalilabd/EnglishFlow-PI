import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ClubService } from './club.service';
import { Club, ClubCategory, ClubStatus, Member, RankType, JoinClubRequest } from '../models/club.model';
import { environment } from '../../../environments/environment';

describe('ClubService', () => {
  let service: ClubService;
  let httpMock: HttpTestingController;

  const mockClub: Club = {
    id: 1,
    name: 'English Conversation Club',
    description: 'Practice speaking English',
    category: ClubCategory.CONVERSATION,
    maxMembers: 50,
    currentMembersCount: 25,
    createdBy: 10,
    creatorName: 'Jane Doe',
    status: ClubStatus.APPROVED,
    createdAt: '2026-01-10T10:00:00',
    updatedAt: '2026-04-01T14:00:00'
  };

  const mockMember: Member = {
    id: 1,
    userId: 5,
    clubId: 1,
    rank: RankType.MEMBER,
    joinedAt: '2026-02-01T10:00:00'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ClubService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ClubService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all clubs', (done) => {
    const mockClubs: Club[] = [mockClub, { ...mockClub, id: 2, name: 'Book Club' }];

    service.getAllClubs().subscribe(clubs => {
      expect(clubs.length).toBe(2);
      expect(clubs).toEqual(mockClubs);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/clubs`);
    expect(req.request.method).toBe('GET');
    req.flush(mockClubs);
  });

  it('should get club by ID', (done) => {
    const clubId = 1;

    service.getClubById(clubId).subscribe(club => {
      expect(club).toEqual(mockClub);
      expect(club.id).toBe(clubId);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/clubs/${clubId}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockClub);
  });

  it('should create a new club', (done) => {
    const newClub = {
      name: 'Writing Workshop',
      description: 'Improve writing skills',
      category: ClubCategory.WRITING,
      maxMembers: 30,
      createdBy: 10
    };

    service.createClub(newClub).subscribe(club => {
      expect(club).toEqual(mockClub);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/clubs`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newClub);
    req.flush(mockClub);
  });

  it('should update a club', (done) => {
    const clubId = 1;
    const userId = 10;
    const updateData = {
      name: 'Updated Club Name',
      description: 'Updated description'
    };

    const updatedClub = { ...mockClub, ...updateData };

    service.updateClub(clubId, updateData, userId).subscribe(club => {
      expect(club.name).toBe('Updated Club Name');
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/clubs/${clubId}?requesterId=${userId}`);
    expect(req.request.method).toBe('PUT');
    req.flush(updatedClub);
  });

  it('should delete a club', (done) => {
    const clubId = 1;

    service.deleteClub(clubId).subscribe(() => {
      expect(true).toBe(true);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/clubs/${clubId}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should get clubs by category', (done) => {
    const category = ClubCategory.CONVERSATION;
    const categoryClubs: Club[] = [mockClub];

    service.getClubsByCategory(category).subscribe(clubs => {
      expect(clubs.length).toBe(1);
      expect(clubs[0].category).toBe(category);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/clubs/category/${category}`);
    expect(req.request.method).toBe('GET');
    req.flush(categoryClubs);
  });

  it('should get available clubs', (done) => {
    const availableClubs: Club[] = [mockClub];

    service.getAvailableClubs().subscribe(clubs => {
      expect(clubs.length).toBe(1);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/clubs/available`);
    req.flush(availableClubs);
  });

  it('should join a club', (done) => {
    const clubId = 1;
    const joinRequest: JoinClubRequest = {
      userId: 5
    };

    service.joinClub(clubId, joinRequest).subscribe(member => {
      expect(member).toEqual(mockMember);
      expect(member.clubId).toBe(clubId);
      expect(member.userId).toBe(5);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/clubs/${clubId}/join`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(joinRequest);
    req.flush(mockMember);
  });

  it('should leave a club', (done) => {
    const clubId = 1;
    const userId = 5;

    service.leaveClub(clubId, userId).subscribe(() => {
      expect(true).toBe(true);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/clubs/${clubId}/leave/${userId}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should get club members', (done) => {
    const clubId = 1;
    const members: Member[] = [
      mockMember,
      { ...mockMember, id: 2, userId: 6, rank: RankType.PRESIDENT }
    ];

    service.getClubMembers(clubId).subscribe(result => {
      expect(result.length).toBe(2);
      expect(result).toEqual(members);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/members/club/${clubId}`);
    expect(req.request.method).toBe('GET');
    req.flush(members);
  });

  it('should approve a club', (done) => {
    const clubId = 1;
    const reviewerId = 20;
    const comment = 'Approved';

    const approvedClub = { ...mockClub, status: ClubStatus.APPROVED };

    service.approveClub(clubId, reviewerId, comment).subscribe(club => {
      expect(club.status).toBe(ClubStatus.APPROVED);
      done();
    });

    const req = httpMock.expectOne(
      req => req.url === `${environment.apiUrl}/clubs/${clubId}/approve`
    );
    expect(req.request.method).toBe('POST');
    req.flush(approvedClub);
  });

  it('should reject a club', (done) => {
    const clubId = 1;
    const reviewerId = 20;
    const comment = 'Rejected';

    const rejectedClub = { ...mockClub, status: ClubStatus.REJECTED };

    service.rejectClub(clubId, reviewerId, comment).subscribe(club => {
      expect(club.status).toBe(ClubStatus.REJECTED);
      done();
    });

    const req = httpMock.expectOne(
      req => req.url === `${environment.apiUrl}/clubs/${clubId}/reject`
    );
    expect(req.request.method).toBe('POST');
    req.flush(rejectedClub);
  });

  it('should notify membership changes', (done) => {
    service.clubMembershipChanged$.subscribe(() => {
      expect(true).toBe(true);
      done();
    });

    service.notifyClubMembershipChanged();
  });

  it('should handle club at max capacity', (done) => {
    const clubId = 1;
    const joinRequest: JoinClubRequest = {
      userId: 5
    };

    service.joinClub(clubId, joinRequest).subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.status).toBe(400);
        done();
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/clubs/${clubId}/join`);
    req.flush({ message: 'Club is full' }, { status: 400, statusText: 'Bad Request' });
  });
});
