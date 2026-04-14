import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { environment } from '../../../environments/environment';

export interface ChatMessage {
  id?: number;
  eventId?: number;
  senderId: number;
  senderName: string;
  content: string;
  translatedContent?: string;
  targetLang?: string;
  moderated?: boolean;
  sentAt?: string;
  isSystem?: boolean;
}

export interface PollOption {
  id: number;
  text: string;
  voteCount: number;
  votedByCurrentUser: boolean;
}

export interface Poll {
  id: number;
  eventId: number;
  question: string;
  multipleChoice: boolean;
  active: boolean;
  options: PollOption[];
}

export interface Question {
  id: number;
  eventId: number;
  authorId?: number;
  authorName: string;
  text: string;
  upvoteCount: number;
  upvotedByCurrentUser: boolean;
  answered: boolean;
  anonymous: boolean;
  createdAt: string;
}

export interface HandRaise {
  id?: number;
  userId: number;
  userName: string;
  raisedAt?: string;
  queue?: HandRaise[];
}

export interface Reaction {
  eventId?: number;
  userId: number;
  userName: string;
  emoji: string;
}

export interface WhiteboardEvent {
  eventId?: number;
  userId: number;
  type: 'DRAW' | 'ERASE' | 'CLEAR' | 'TEXT' | 'TAB_SWITCH' | 'SCREEN_SHARE';
  x: number;
  y: number;
  x2?: number;
  y2?: number;
  color?: string;
  strokeWidth?: number;
  text?: string;
}

export interface ConnectedUser {
  userId: number;
  userName: string;
  joinedAt: string;
  systemRole?: string;
  profilePhoto?: string;
}

@Injectable({ providedIn: 'root' })
export class LiveSessionService {

  private wsEndpoint = `${environment.apiUrl.replace('/api', '')}/event-service/ws/event`;
  private apiBase = `${environment.apiUrl}/events`;

  messages$ = new BehaviorSubject<ChatMessage[]>([]);
  poll$ = new BehaviorSubject<Poll | null>(null);
  questions$ = new BehaviorSubject<Question[]>([]);
  handQueue$ = new BehaviorSubject<HandRaise[]>([]);
  reactions$ = new BehaviorSubject<Reaction | null>(null);
  whiteboard$ = new BehaviorSubject<WhiteboardEvent | null>(null);
  connected$ = new BehaviorSubject<boolean>(false);
  connectedUsers$ = new BehaviorSubject<ConnectedUser[]>([]);

  private client: Client | null = null;
  get stompClient(): Client | null { return this.client; }
  private currentEventId: number | null = null;
  private currentUserId: number | null = null;

  constructor(private http: HttpClient) {}

  connect(eventId: number, userId?: number, userName?: string, systemRole?: string, profilePhoto?: string): Promise<void> {
    this.currentEventId = eventId;
    this.currentUserId = userId ?? null;
    return new Promise((resolve, reject) => {
      this.client = new Client({
        webSocketFactory: () => new SockJS(this.wsEndpoint),
        reconnectDelay: 5000,
        onConnect: () => {
          this.connected$.next(true);
          this.subscribeAll(eventId);
          this.loadHistory(eventId);
          if (userId && userName) {
            this.send(`/app/session/${eventId}/presence`, { userId, userName, action: 'JOIN', systemRole: systemRole ?? 'STUDENT', profilePhoto: profilePhoto ?? null });
          }
          resolve();
        },
        onStompError: (frame) => reject(new Error(frame.headers['message']))
      });
      this.client.activate();
    });
  }

  private subscribeAll(eventId: number): void {
    const base = `/topic/session/${eventId}`;

    this.client!.subscribe(`${base}/chat`, (m: IMessage) => {
      const msg: ChatMessage = JSON.parse(m.body);
      if (msg.moderated) {
        this.messages$.next(this.messages$.value.filter(x => x.id !== msg.id));
      } else {
        this.messages$.next([...this.messages$.value, msg]);
      }
    });

    this.client!.subscribe(`${base}/poll`, (m: IMessage) => {
      this.poll$.next(JSON.parse(m.body));
    });

    this.client!.subscribe(`${base}/qa`, (m: IMessage) => {
      this.questions$.next(JSON.parse(m.body));
    });

    this.client!.subscribe(`${base}/hands`, (m: IMessage) => {
      const data: HandRaise = JSON.parse(m.body);
      this.handQueue$.next(data.queue || []);
    });

    this.client!.subscribe(`${base}/reactions`, (m: IMessage) => {
      this.reactions$.next(JSON.parse(m.body));
    });

    this.client!.subscribe(`${base}/whiteboard`, (m: IMessage) => {
      this.whiteboard$.next(JSON.parse(m.body));
    });

    this.client!.subscribe(`${base}/presence`, (m: IMessage) => {
      const data: { userId: number; userName: string; action: 'JOIN' | 'LEAVE'; joinedAt: string; systemRole?: string; profilePhoto?: string } = JSON.parse(m.body);
      if (data.userId === this.currentUserId) return;
      const current = this.connectedUsers$.value;
      if (data.action === 'JOIN') {
        if (!current.find(u => u.userId === data.userId)) {
          this.connectedUsers$.next([...current, { userId: data.userId, userName: data.userName, joinedAt: data.joinedAt, systemRole: data.systemRole, profilePhoto: data.profilePhoto }]);
        }
      } else {
        this.connectedUsers$.next(current.filter(u => u.userId !== data.userId));
      }
    });
  }

  private loadHistory(eventId: number): void {
    this.messages$.next([]);
    this.http.get<Question[]>(`${this.apiBase}/${eventId}/session/questions`)
      .subscribe(qs => this.questions$.next(qs));
    this.http.get<Poll>(`${this.apiBase}/${eventId}/session/polls/active?userId=${this.currentUserId ?? 0}`)
      .subscribe({ next: p => this.poll$.next(p), error: () => {} });
    this.http.get<{ userId: number; userName: string; joinedAt: string; systemRole?: string; profilePhoto?: string }[]>(
      `${this.apiBase}/${eventId}/session/participants`
    ).subscribe({
      next: list => {
        const current = this.connectedUsers$.value;
        const merged = [...current];
        list.forEach(u => {
          if (u.userId !== this.currentUserId && !merged.find(x => x.userId === u.userId)) {
            merged.push({ userId: u.userId, userName: u.userName, joinedAt: u.joinedAt, systemRole: u.systemRole, profilePhoto: u.profilePhoto });
          }
        });
        this.connectedUsers$.next(merged);
      },
      error: () => {}
    });
  }

  private send(destination: string, body: object): void {
    this.client?.publish({ destination, body: JSON.stringify(body) });
  }

  sendMessage(msg: ChatMessage): void {
    this.send(`/app/session/${this.currentEventId}/chat`, msg);
  }

  sendSystemMessageLocal(msg: ChatMessage): void {
    this.messages$.next([...this.messages$.value, { ...msg, id: Date.now() }]);
  }

  raiseHand(userId: number, userName: string): void {
    this.send(`/app/session/${this.currentEventId}/hand/raise`, { userId, userName });
  }

  dismissHand(userId: number): void {
    this.send(`/app/session/${this.currentEventId}/hand/dismiss`, { userId });
  }

  vote(pollId: number, optionId: number, userId: number): void {
    this.send(`/app/session/${this.currentEventId}/poll/vote`, { pollId, optionId, userId });
  }

  askQuestion(authorId: number, authorName: string, text: string, anonymous: boolean): void {
    this.send(`/app/session/${this.currentEventId}/qa/ask`, { authorId, authorName, text, anonymous });
  }

  upvoteQuestion(questionId: number, userId: number): void {
    this.send(`/app/session/${this.currentEventId}/qa/upvote`, { id: questionId, authorId: userId });
  }

  markAnswered(questionId: number): void {
    this.send(`/app/session/${this.currentEventId}/qa/answered`, { id: questionId });
  }

  sendReaction(userId: number, userName: string, emoji: string): void {
    this.send(`/app/session/${this.currentEventId}/reaction`, { userId, userName, emoji });
  }

  sendWhiteboardEvent(event: WhiteboardEvent): void {
    this.send(`/app/session/${this.currentEventId}/whiteboard`, event);
  }

  broadcastTabSwitch(tab: string): void {
    this.send(`/app/session/${this.currentEventId}/whiteboard`, {
      userId: this.currentUserId ?? 0,
      type: 'TAB_SWITCH',
      x: 0, y: 0,
      text: tab
    } as WhiteboardEvent);
  }

  broadcastScreenShare(active: boolean, sharerUserId: number): void {
    this.send(`/app/session/${this.currentEventId}/whiteboard`, {
      userId: this.currentUserId ?? 0,
      type: 'SCREEN_SHARE',
      x: 0, y: 0,
      text: active ? String(sharerUserId) : ''
    } as WhiteboardEvent);
  }

  createPoll(eventId: number, question: string, options: string[], multipleChoice: boolean): Observable<Poll> {
    return this.http.post<Poll>(`${this.apiBase}/${eventId}/session/polls`,
      { question, options, multipleChoice });
  }

  moderateMessage(eventId: number, messageId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiBase}/${eventId}/session/chat/${messageId}`);
  }

  disconnect(userId?: number, userName?: string): void {
    if (userId && userName) {
      this.send(`/app/session/${this.currentEventId}/presence`, { userId, userName, action: 'LEAVE' });
    }
    this.client?.deactivate();
    this.client = null;
    this.connected$.next(false);
    this.connectedUsers$.next([]);
    this.messages$.next([]);
    this.currentEventId = null;
  }
}
