export interface UserProfile {
  id: string;
  displayName: string;
  email?: string | null;
  avatarUrl?: string | null;
  authProvider: string;
  lastLoginAt?: string | null;
}

export interface UserCreditSummary {
  userId: string;
  dailyLimit: number;
  usedToday: number;
  remainingToday: number;
  usageDate: string;
  canGenerate: boolean;
}
