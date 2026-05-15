import {Injectable} from '@angular/core';

@Injectable({ providedIn: 'root' })
export class TokenService {
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USERNAME_KEY = 'auth_username';
  private readonly ROLE_KEY = 'auth_role';

  private get storage(): Storage | null {
    return typeof window !== 'undefined' ? window.localStorage : null;
  }

  setToken(token: string): void {
    this.storage?.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string | null {
    return this.storage?.getItem(this.TOKEN_KEY) ?? null;
  }

  removeToken(): void {
    this.storage?.removeItem(this.TOKEN_KEY);
  }

  setUsername(username: string): void {
    this.storage?.setItem(this.USERNAME_KEY, username);
  }

  getUsername(): string {
    return this.storage?.getItem(this.USERNAME_KEY) ?? 'Invitado';
  }

  removeUsername(): void {
    this.storage?.removeItem(this.USERNAME_KEY);
  }

  setRole(role: string | null): void {
    if (role) {
      this.storage?.setItem(this.ROLE_KEY, role);
    } else {
      this.storage?.removeItem(this.ROLE_KEY);
    }
  }

  getRole(): string | null {
    return this.storage?.getItem(this.ROLE_KEY) ?? null;
  }

  removeRole(): void {
    this.storage?.removeItem(this.ROLE_KEY);
  }

  getRolesFromToken(token?: string | null): string[] {
    const payload = this.getTokenPayload(token ?? this.getToken());
    const roles = payload?.['roles'];

    if (Array.isArray(roles)) return roles;
    if (typeof roles === 'string') return [roles];

    return [];
  }

  getPrimaryRole(token?: string | null): string | null {
    const stored = this.getRole();
    if (stored) return stored;
    return this.getRolesFromToken(token ?? this.getToken())[0] ?? null;
  }

  private getTokenPayload(token?: string | null): Record<string, unknown> | null {
    if (!token) return null;

    try {
      const payload = token.split('.')[1];
      return JSON.parse(atob(payload));
    } catch {
      return null;
    }
  }

  isTokenExpired(): boolean {
    const token = this.getToken();
    if (!token) return true;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expirationMs = payload.exp * 1000;
      return Date.now() >= expirationMs;
    } catch {
      return true;
    }
  }

  isLogged(): boolean {
    return !!this.getToken() && !this.isTokenExpired();
  }

  clearSession(): void {
    this.removeToken();
    this.removeUsername();
    this.removeRole();
  }
}
