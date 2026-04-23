import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';
import { Observable } from 'rxjs';
import IUser from '../model/IUser';

export type SettingsRole = 'artist' | 'listener';

export interface AccountSettings {
  email: string;
  password?: string;
}

export interface ArtistProfileSettings {
  bio: string;
  instagram: string;
  youtube: string;
  tiktok: string;
}

export interface ListenerProfileSettings {
  favoriteGenres: string;
  explicitContent: boolean;
  releaseNotifications: boolean;
}

export interface UserSettings {
  account: AccountSettings;
  role: SettingsRole;
  profile: ArtistProfileSettings | ListenerProfileSettings;
}

export interface UpdateUserSettingsPayload {
  account: AccountSettings;
  role: SettingsRole;
  profile: ArtistProfileSettings | ListenerProfileSettings;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly baseURL = `${environment.apiURL}/users`;
  private readonly settingsURL = `${this.baseURL}/me/settings`;

  getAll(): Observable<IUser[]> {
    return this.http.get<IUser[]>(this.baseURL);
  }

  getById(id: number): Observable<IUser> {
    return this.http.get<IUser>(`${this.baseURL}/${id}`);
  }

  getSettings(): Observable<UserSettings> {
    return this.http.get<UserSettings>(this.settingsURL);
  }

  updateSettings(payload: UpdateUserSettingsPayload): Observable<UserSettings> {
    return this.http.put<UserSettings>(this.settingsURL, payload);
  }
}
