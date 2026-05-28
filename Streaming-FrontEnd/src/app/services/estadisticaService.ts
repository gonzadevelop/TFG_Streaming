import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { IEstadistica, ITopAlbum, ITopArtista, ITopCancion } from '../model/estadistica/IEstadistica';

@Injectable({ providedIn: 'root' })
export class EstadisticaService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  /**
   * Devuelve los minutos escuchados por el usuario en el mes actual.
   * GET /api/estadisticas/minutos-mes
   */
  getMinutosMes(): Observable<unknown> {
    return this.http.get<unknown>(`${this.baseUrl}/estadisticas/minutos-mes`);
  }

  /**
   * Devuelve el Top 5 de canciones más escuchadas en el mes actual.
   * GET /api/estadisticas/top-canciones
   */
  getTopCanciones(): Observable<ITopCancion[]> {
    return this.http.get<ITopCancion[]>(`${this.baseUrl}/estadisticas/top-canciones`);
  }

  /**
   * Devuelve el Top 5 de artistas más escuchados en el mes actual.
   * GET /api/estadisticas/top-artistas
   */
  getTopArtistas(): Observable<ITopArtista[]> {
    return this.http.get<ITopArtista[]>(`${this.baseUrl}/estadisticas/top-artistas`);
  }

  /**
   * Devuelve el Top 5 de álbumes más escuchados en el mes actual.
   * GET /api/estadisticas/top-albumes
   */
  getTopAlbumes(): Observable<ITopAlbum[]> {
    return this.http.get<ITopAlbum[]>(`${this.baseUrl}/estadisticas/top-albumes`);
  }
}
