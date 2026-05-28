import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { IPlaylistCompleta } from '../model/playlists/IPlaylistCompleta';

@Injectable({ providedIn: 'root' })
export class KeySoundPlaylistsService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  /**
   * Top 30 diario. Si no se pasa fecha, el backend usa su lógica por defecto.
   * Formato fecha: YYYY-MM-DD (ej: 2026-04-22)
   */
  getDailyTop30(fechaYYYYMMDD?: string): Observable<IPlaylistCompleta> {
    const url = fechaYYYYMMDD
      ? `${this.baseUrl}/KeySoundPlaylists/dailyTop30/${fechaYYYYMMDD}`
      : `${this.baseUrl}/KeySoundPlaylists/dailyTop30`;

    return this.http.get<IPlaylistCompleta>(url);
  }

  /**
   * Obtiene una playlist por su nombre (slug) de ruta.
   * Mapea el nombre del segmento de URL al endpoint correcto del backend.
   */
  getPlaylistByNombre(nombre: string, fechaYYYYMMDD?: string): Observable<IPlaylistCompleta> {

    const key = nombre
      .toLowerCase()
      .normalize('NFD').replace(/[\u0300-\u036f]/g, '')
      .replace(/[\s\-_]/g, '');

    if (key.includes('top30')) {
      return this.getDailyTop30(fechaYYYYMMDD);
    }
    return this.http.get<IPlaylistCompleta>(`${this.baseUrl}/KeySoundPlaylists/${encodeURIComponent(nombre)}`);
  }
}
