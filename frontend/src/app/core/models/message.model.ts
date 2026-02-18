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
  isEdited: boolean;
  createdAt: Date;
  updatedAt: Date;
  readBy?: MessageReadStatus[];
}

export enum MessageType {
  TEXT = 'TEXT',
  FILE = 'FILE',
  IMAGE = 'IMAGE'
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
