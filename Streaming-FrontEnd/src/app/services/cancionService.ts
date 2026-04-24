import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../environments/environment';
import {Observable} from 'rxjs';
import {IPistaPlaylist} from '../model/pista/IPistaPlaylist';

@Injectable({ providedIn: 'root' })
export class CancionService {
  private readonly http = inject(HttpClient);
  private readonly baseURL = `${environment.apiURL}`;

  /**
   * Registra la reproducción de una canción en el historial del usuario.
   * DEBE ejecutarse cuando el usuario llega al segundo 30 de la canción.
   * POST /api/canciones/reproducir
   * @param pistaId ID de la pista a reproducir
   */
  setReproducirCancion(pistaId: number): Observable<void> {
    return this.http.post<void>(`${this.baseURL}/canciones/reproducir`, pistaId);
  }

  /**
   * Devuelve las canciones más reproducidas del usuario autenticado.
   * GET /api/canciones/mis-canciones-mas-reproducidas
   */
  getCancionesMasReproducidas(): Observable<IPistaPlaylist[]> {
    return this.http.get<IPistaPlaylist[]>(`${this.baseURL}/canciones/mis-canciones-mas-reproducidas`);
  }
}
