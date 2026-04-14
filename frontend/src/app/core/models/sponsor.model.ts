export interface Sponsor {
  id?: number;
  name: string;
  description?: string;
  logo?: string;
  website?: string;
  contactEmail?: string;
  contactPhone?: string;
  level?: SponsorLevel;
  status?: 'PENDING' | 'APPROVED' | 'REJECTED';
  contributionAmount?: number;
  userId?: number;
  applicantFirstName?: string;
  applicantLastName?: string;
  clubId?: number;
  clubName?: string;
  createdAt?: string;
  updatedAt?: string;
}

export enum SponsorLevel {
  GOLD = 'GOLD',
  SILVER = 'SILVER',
  BRONZE = 'BRONZE'
}

export interface CreateSponsorRequest {
  name: string;
  description?: string;
  logo?: string;
  website?: string;
  contactEmail?: string;
  contactPhone?: string;
  level?: SponsorLevel;
  contributionAmount?: number;
}
