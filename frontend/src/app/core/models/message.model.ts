export interface Message {
  id: number;
  conversationId: number;
  senderId: number;
  senderName: string;
  senderAvatar?: string;
  content: string;
  messageType: MessageType;
  fileUrl?: string;
  fileName?: string;
  fileSize?: number;
  emojiCode?: string;
  voiceDuration?: number;
  isEdited: boolean;
  createdAt: Date;
  updatedAt: Date;
  readBy?: MessageReadStatus[];
  reactions?: ReactionSummary[];
  status?: MessageStatus;
}

export enum MessageType {
  TEXT = 'TEXT',
  FILE = 'FILE',
  IMAGE = 'IMAGE',
  EMOJI = 'EMOJI',
  VOICE = 'VOICE'
}

export enum MessageStatus {
  SENT = 'SENT',
  DELIVERED = 'DELIVERED',
  READ = 'READ'
}

export interface MessageReadStatus {
  userId: number;
  userName: string;
  readAt: Date;
}

export interface SendMessageRequest {
  content: string;
  messageType: MessageType;
  fileUrl?: string;
  fileName?: string;
  fileSize?: number;
  emojiCode?: string;
  voiceDuration?: number;
}

export interface TypingIndicator {
  conversationId: number;
  userId: number;
  userName: string;
  isTyping: boolean;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface ReactionSummary {
  emoji: string;
  count: number;
  userNames: string[];
  reactedByCurrentUser: boolean;
}

export interface AddReactionRequest {
  emoji: string;
}
