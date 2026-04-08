/**
 * WebSocket Models and Interfaces
 * Centralized type definitions for WebSocket communications
 */

// ============================================
// Club WebSocket Models
// ============================================

export interface ClubNotification {
  type: ClubNotificationType;
  clubId: number;
  clubName: string;
  message: string;
  data?: any;
  timestamp: string;
}

export type ClubNotificationType =
  | 'CLUB_CREATED'
  | 'CLUB_UPDATED'
  | 'CLUB_DELETED'
  | 'MEMBER_JOINED'
  | 'MEMBER_LEFT'
  | 'MEMBER_PROMOTED'
  | 'MEMBER_DEMOTED'
  | 'TASK_CREATED'
  | 'TASK_UPDATED'
  | 'TASK_COMPLETED'
  | 'SUBSCRIPTION_CONFIRMED';

export interface MemberActivity {
  activityType: MemberActivityType;
  clubId: number;
  userId: number;
  userName: string;
  role: string;
  timestamp: string;
}

export type MemberActivityType = 'JOINED' | 'LEFT' | 'PROMOTED' | 'DEMOTED';

// ============================================
// Event WebSocket Models
// ============================================

export interface EventNotification {
  type: EventNotificationType;
  eventId: number;
  eventTitle: string;
  message: string;
  data?: any;
  timestamp: string;
}

export type EventNotificationType =
  | 'EVENT_CREATED'
  | 'EVENT_UPDATED'
  | 'EVENT_CANCELLED'
  | 'EVENT_STARTED'
  | 'EVENT_ENDED'
  | 'PARTICIPANT_JOINED'
  | 'PARTICIPANT_LEFT'
  | 'PARTICIPANT_CHECKED_IN'
  | 'EVENT_FULL'
  | 'SUBSCRIPTION_CONFIRMED';

export interface ParticipantActivity {
  activityType: ParticipantActivityType;
  eventId: number;
  userId: number;
  userName: string;
  currentParticipants: number;
  maxParticipants: number;
  timestamp: string;
}

export type ParticipantActivityType = 'JOINED' | 'LEFT' | 'CHECKED_IN';

// ============================================
// WebSocket Configuration
// ============================================

export interface WebSocketConfig {
  endpoint: string;
  reconnectDelay?: number;
  heartbeatIncoming?: number;
  heartbeatOutgoing?: number;
  debug?: boolean;
}

export interface WebSocketConnectionStatus {
  connected: boolean;
  lastConnected?: Date;
  lastDisconnected?: Date;
  reconnectAttempts?: number;
}

// ============================================
// Notification Display Models
// ============================================

export interface NotificationDisplay {
  id: string;
  title: string;
  message: string;
  type: 'success' | 'info' | 'warning' | 'error';
  icon?: string;
  timestamp: Date;
  read: boolean;
  data?: any;
}

export interface NotificationPreferences {
  enableSound: boolean;
  enableDesktopNotifications: boolean;
  enableToasts: boolean;
  mutedClubs: number[];
  mutedEvents: number[];
}

// ============================================
// WebSocket Message Types
// ============================================

export interface WebSocketMessage<T = any> {
  destination: string;
  body: T;
  headers?: Record<string, string>;
}

export interface WebSocketSubscription {
  id: string;
  destination: string;
  callback: (message: any) => void;
  active: boolean;
}

// ============================================
// Error Models
// ============================================

export interface WebSocketError {
  code: string;
  message: string;
  timestamp: Date;
  details?: any;
}

export enum WebSocketErrorCode {
  CONNECTION_FAILED = 'CONNECTION_FAILED',
  SUBSCRIPTION_FAILED = 'SUBSCRIPTION_FAILED',
  MESSAGE_SEND_FAILED = 'MESSAGE_SEND_FAILED',
  AUTHENTICATION_FAILED = 'AUTHENTICATION_FAILED',
  TIMEOUT = 'TIMEOUT',
  UNKNOWN = 'UNKNOWN'
}
