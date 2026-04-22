import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { IPlaylistCompleta } from '../model/playlists/IPlaylistCompleta';

@Injectable({ providedIn: 'root' })
export class KeySoundPlaylistsService {
  private readonly http = inject(HttpClient);
  private readonly baseURL = `${environment.apiURL}`;

  /**
   * Top 30 diario. Si no se pasa fecha, el backend usa su lógica por defecto.
   * Formato fecha: YYYY-MM-DD (ej: 2026-04-22)
   */
  getDailyTop30(fechaYYYYMMDD?: string): Observable<IPlaylistCompleta> {
    const url = fechaYYYYMMDD
      ? `${this.baseURL}/KeySoundPlaylists/dailyTop30/${fechaYYYYMMDD}`
      : `${this.baseURL}/KeySoundPlaylists/dailyTop30`;

    return this.http.get<IPlaylistCompleta>(url);
  }
}

