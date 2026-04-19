export interface AuthUser {
  id: string;
  email?: string | null;
  displayName: string;
  avatarUrl?: string | null;
  authProvider: string;
  lastLoginAt?: string | null;
}

export interface CurrentUserResponse {
  authenticated: boolean;
  user: AuthUser | null;
}

export interface AuthStatusResponse extends CurrentUserResponse {}
