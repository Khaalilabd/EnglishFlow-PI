export interface Club {
  id?: number;
  name: string;
  description: string;
  objective?: string;
  category: ClubCategory;
  maxMembers: number;
  image?: string; // Base64 encoded image
  members?: Member[];
  createdAt?: string;
  updatedAt?: string;
  currentMembersCount?: number;
  isFull?: boolean;
}

export enum ClubCategory {
  CONVERSATION = 'CONVERSATION',
  BOOK = 'BOOK',
  DRAMA = 'DRAMA',
  WRITING = 'WRITING'
}

export interface Member {
  id?: number;
  rank: RankType;
  userId: number;
  clubId?: number;
  joinedAt?: string;
  updatedAt?: string;
}

export enum RankType {
  MEMBER = 'MEMBER',
  SECRETARY = 'SECRETARY',
  PRESIDENT = 'PRESIDENT'
}

export interface CreateClubRequest {
  name: string;
  description: string;
  objective?: string;
  category: ClubCategory;
  maxMembers: number;
  image?: string;
}

export interface UpdateClubRequest {
  name?: string;
  description?: string;
  objective?: string;
  category?: ClubCategory;
  maxMembers?: number;
  image?: string;
}

export interface JoinClubRequest {
  userId: number;
}
