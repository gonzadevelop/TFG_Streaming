import {Injectable} from '@angular/core';

@Injectable({ providedIn: 'root' })
export class TokenService {
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USERNAME_KEY = 'auth_username';

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
  }
}
