export type UserRole = "student" | "lecturer" | "admin";

export interface Role {
  id: number;
  name: string;
  description?: string;
  active?: boolean;
}

export interface User {
  id: number;
  name: string;
  email: string;
  role: UserRole;
  avatar?: string;
}

export interface BackendUser {
  id: number;
  email: string;
  name: string;
  role?: Role;
}

export interface LoginResponse {
  access_token: string;
  user: BackendUser;
}

export interface AccountResponse {
  user: BackendUser;
}

export interface RegisterResponse {
  id: number;
  email: string;
  name: string;
}
