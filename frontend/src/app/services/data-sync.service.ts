import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { ClubWebSocketService } from './club-websocket.service';
import { EventWebSocketService } from './event-websocket.service';
import { SponsorWebSocketService } from './sponsor-websocket.service';

/**
 * Service pour synchroniser automatiquement les données via WebSocket
 */
@Injectable({
  providedIn: 'root'
})
export class DataSyncService {
  // Sujets pour notifier les composants de rafraîchir leurs données
  private clubDataChanged$ = new BehaviorSubject<{ clubId?: number; action: string }>({ action: 'none' });
  private eventDataChanged$ = new BehaviorSubject<{ eventId?: number; action: string }>({ action: 'none' });
  private memberDataChanged$ = new BehaviorSubject<{ clubId: number; action: string } | null>(null);
  private participantDataChanged$ = new BehaviorSubject<{ eventId: number; action: string } | null>(null);
  private sponsorDataChanged$ = new BehaviorSubject<{ sponsorId?: number; action: string }>({ action: 'none' });

  constructor(
    private clubWsService: ClubWebSocketService,
    private eventWsService: EventWebSocketService,
    private sponsorWsService: SponsorWebSocketService
  ) {
    this.initializeClubSync();
    this.initializeEventSync();
    this.initializeSponsorSync();
  }

  /**
   * Initialiser la synchronisation des clubs
   */
  private initializeClubSync() {
    // Écouter les notifications globales des clubs
    this.clubWsService.getGlobalNotifications().subscribe(notification => {
      if (notification) {
        console.log('🔄 Club data sync triggered:', notification.type);
        this.clubDataChanged$.next({
          clubId: notification.clubId,
          action: notification.type
        });
      }
    });

    // Écouter les notifications spécifiques aux clubs
    this.clubWsService.getClubNotifications().subscribe(notification => {
      if (notification) {
        console.log('🔄 Club specific data sync triggered:', notification.type);
        this.clubDataChanged$.next({
          clubId: notification.clubId,
          action: notification.type
        });
      }
    });

    // Écouter les activités des membres
    this.clubWsService.getMemberActivities().subscribe(activity => {
      if (activity) {
        console.log('🔄 Member data sync triggered:', activity.activityType);
        this.memberDataChanged$.next({
          clubId: activity.clubId,
          action: activity.activityType
        });
      }
    });
  }

  /**
   * Initialiser la synchronisation des événements
   */
  private initializeEventSync() {
    // Écouter les notifications globales des événements
    this.eventWsService.getGlobalNotifications().subscribe(notification => {
      if (notification) {
        console.log('🔄 Event data sync triggered:', notification.type);
        this.eventDataChanged$.next({
          eventId: notification.eventId,
          action: notification.type
        });
      }
    });

    // Écouter les notifications spécifiques aux événements
    this.eventWsService.getEventNotifications().subscribe(notification => {
      if (notification) {
        console.log('🔄 Event specific data sync triggered:', notification.type);
        this.eventDataChanged$.next({
          eventId: notification.eventId,
          action: notification.type
        });
      }
    });

    // Écouter les activités des participants
    this.eventWsService.getParticipantActivities().subscribe(activity => {
      if (activity) {
        console.log('🔄 Participant data sync triggered:', activity.activityType);
        this.participantDataChanged$.next({
          eventId: activity.eventId,
          action: activity.activityType
        });
      }
    });
  }

  /**
   * Observer les changements de données des clubs
   */
  onClubDataChanged(): Observable<{ clubId?: number; action: string }> {
    return this.clubDataChanged$.asObservable();
  }

  /**
   * Observer les changements de données des événements
   */
  onEventDataChanged(): Observable<{ eventId?: number; action: string }> {
    return this.eventDataChanged$.asObservable();
  }

  /**
   * Observer les changements de membres
   */
  onMemberDataChanged(): Observable<{ clubId: number; action: string } | null> {
    return this.memberDataChanged$.asObservable();
  }

  /**
   * Observer les changements de participants
   */
  onParticipantDataChanged(): Observable<{ eventId: number; action: string } | null> {
    return this.participantDataChanged$.asObservable();
  }

  /**
   * Forcer un rafraîchissement des données d'un club
   */
  refreshClubData(clubId: number) {
    this.clubDataChanged$.next({ clubId, action: 'MANUAL_REFRESH' });
  }

  /**
   * Forcer un rafraîchissement des données d'un événement
   */
  refreshEventData(eventId: number) {
    this.eventDataChanged$.next({ eventId, action: 'MANUAL_REFRESH' });
  }
  
  /**
   * Initialiser la synchronisation des sponsors
   */
  private initializeSponsorSync() {
    this.sponsorWsService.getSponsorNotifications().subscribe(notification => {
      if (notification) {
        console.log('🔄 Sponsor data sync triggered:', notification.type);
        this.sponsorDataChanged$.next({
          sponsorId: notification.sponsorId,
          action: notification.type
        });
      }
    });
  }
  
  /**
   * Observer les changements de données des sponsors
   */
  onSponsorDataChanged(): Observable<{ sponsorId?: number; action: string }> {
    return this.sponsorDataChanged$.asObservable();
  }
  
  /**
   * Forcer un rafraîchissement des données d'un sponsor
   */
  refreshSponsorData(sponsorId?: number) {
    this.sponsorDataChanged$.next({ sponsorId, action: 'MANUAL_REFRESH' });
  }
}
