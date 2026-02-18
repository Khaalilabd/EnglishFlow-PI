import { Message } from './message.model';

export interface Conversation {
  id: number;
  type: ConversationType;
  title?: string;
  participants: ConversationParticipant[];
  lastMessage?: Message;
  unreadCount: number;
  createdAt: Date;
  lastMessageAt?: Date;
}

export enum ConversationType {
  DIRECT = 'DIRECT',
  GROUP = 'GROUP'
}

export interface ConversationParticipant {
  userId: number;
  userName: string;
  userEmail: string;
  userRole: string;
  userAvatar?: string;
  isOnline: boolean;
  lastReadAt?: Date;
}

export interface CreateConversationRequest {
  participantIds: number[];
  type: ConversationType;
  title?: string;
}
