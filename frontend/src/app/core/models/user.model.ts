export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  cin?: string;
  role: UserRole;
  isActive: boolean;
  registrationFeePaid: boolean;
  createdAt: Date;
}

export enum UserRole {
  ADMIN = 'ADMIN',
  TUTOR = 'TUTOR',
  TEACHER = 'TEACHER',
  STUDENT = 'STUDENT',
  ACADEMIC_OFFICE_AFFAIR = 'ACADEMIC_OFFICE_AFFAIR'
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone?: string;
  cin?: string;
  dateOfBirth?: string;
  address?: string;
  city?: string;
  postalCode?: string;
  bio?: string;
  englishLevel?: string;
  yearsOfExperience?: number;
  role: string;
}

export interface AuthResponse {
  token: string;
  refreshToken?: string;
  type: string;
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  profilePhoto?: string | null;
  phone?: string;
  cin?: string;
  dateOfBirth?: string;
  address?: string;
  city?: string;
  postalCode?: string;
  bio?: string;
  englishLevel?: string;
  profileCompleted?: boolean;
  expiresIn?: number;
  refreshTokenExpiryDate?: string;
}
