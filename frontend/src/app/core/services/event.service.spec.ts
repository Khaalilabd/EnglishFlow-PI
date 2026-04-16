import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { EventService, Event, Participant } from './event.service';
import { environment } from '../../../environments/environment';

describe('EventService', () => {
  let service: EventService;
  let httpMock: HttpTestingController;

  const mockEvent: Event = {
    id: 1,
    title: 'English Workshop',
    type: 'WORKSHOP',
    format: 'IN_PERSON',
    startDate: '2026-05-01T14:00:00',
    endDate: '2026-05-01T17:00:00',
    location: 'Community Center',
    maxParticipants: 30,
    currentParticipants: 15,
    participationFee: 50,
    description: 'Interactive English workshop',
    creatorId: 10,
    status: 'APPROVED',
    createdAt: '2026-04-01T10:00:00'
  };

  const mockParticipant: Participant = {
    id: 1,
    eventId: 1,
    userId: 5,
    userEmail: 'john@example.com',
    userFirstName: 'John',
    userLastName: 'Doe',
    joinDate: '2026-04-10T10:00:00'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        EventService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(EventService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all events', (done) => {
    const mockEvents: Event[] = [mockEvent];

    service.getAllEvents().subscribe(events => {
      expect(events.length).toBe(1);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/events`);
    expect(req.request.method).toBe('GET');
    req.flush(mockEvents);
  });

  it('should get event by ID', (done) => {
    const eventId = 1;

    service.getEventById(eventId).subscribe(event => {
      expect(event).toEqual(mockEvent);
      expect(event.id).toBe(eventId);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/events/${eventId}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockEvent);
  });

  it('should create a new event', (done) => {
    const newEvent: Event = {
      title: 'New Workshop',
      type: 'WORKSHOP',
      format: 'ONLINE',
      startDate: '2026-06-01T14:00:00',
      endDate: '2026-06-01T16:00:00',
      location: 'Online',
      maxParticipants: 50
    };

    service.createEvent(newEvent).subscribe(event => {
      expect(event).toEqual(mockEvent);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/events`);
    expect(req.request.method).toBe('POST');
    req.flush(mockEvent);
  });

  it('should update an event', (done) => {
    const eventId = 1;
    const updateData: Event = {
      ...mockEvent,
      title: 'Updated Workshop'
    };

    service.updateEvent(eventId, updateData).subscribe(event => {
      expect(event.title).toBe('Updated Workshop');
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/events/${eventId}`);
    expect(req.request.method).toBe('PUT');
    req.flush(updateData);
  });

  it('should delete an event', (done) => {
    const eventId = 1;

    service.deleteEvent(eventId).subscribe(() => {
      expect(true).toBe(true);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/events/${eventId}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should get upcoming events', (done) => {
    const upcomingEvents: Event[] = [mockEvent];

    service.getUpcomingEvents().subscribe(events => {
      expect(events.length).toBe(1);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/events/upcoming`);
    req.flush(upcomingEvents);
  });

  it('should join an event', (done) => {
    const eventId = 1;
    const userId = 5;

    service.joinEvent(eventId, userId).subscribe(participant => {
      expect(participant).toEqual(mockParticipant);
      expect(participant.eventId).toBe(eventId);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/events/${eventId}/join`);
    expect(req.request.method).toBe('POST');
    req.flush(mockParticipant);
  });

  it('should leave an event', (done) => {
    const eventId = 1;
    const userId = 5;

    service.leaveEvent(eventId, userId).subscribe(() => {
      expect(true).toBe(true);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/events/${eventId}/leave/${userId}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should get event participants', (done) => {
    const eventId = 1;
    const participants: Participant[] = [mockParticipant];

    service.getEventParticipants(eventId).subscribe(result => {
      expect(result.length).toBe(1);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/events/${eventId}/participants`);
    req.flush(participants);
  });

  it('should notify participation changes', (done) => {
    service.eventParticipationChanged$.subscribe(() => {
      expect(true).toBe(true);
      done();
    });

    service.notifyEventParticipationChanged();
  });

  it('should handle event at max capacity', (done) => {
    const eventId = 1;
    const userId = 5;

    service.joinEvent(eventId, userId).subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.status).toBe(400);
        done();
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/events/${eventId}/join`);
    req.flush({ message: 'Event is full' }, { status: 400, statusText: 'Bad Request' });
  });
});
