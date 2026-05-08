import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../environments/environment';
import {Observable, of} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import { IPlaylistRequest, ICancionesPlaylistRequest } from '../model/playlists/IPlaylistRequest';
import {IPlaylistCompleta} from '../model/playlists/IPlaylistCompleta';
import { IPlaylist } from '../model/home/IPlaylist';
import { IPista } from '../model/pista/IPista';

@Injectable({ providedIn: 'root' })
export class PlaylistService {
  private readonly http = inject(HttpClient);
  private readonly baseURL = `${environment.apiURL}`;

  /**
   * Obtiene todas las playlists destacadas de KeySound.
   * GET /api/playlists/keysound
   */
  getPlaylistsKeysound(): Observable<IPlaylist[]> {
    return this.http.get<IPlaylist[]>(`${this.baseURL}/playlists/keysound`);
  }

  /**
   * El usuario crea una nueva playlist vacía.
   * El backend usa @ModelAttribute (multipart/form-data).
   * POST /api/playlists/crear
   */
  setCrearPlaylist(dto: IPlaylistRequest): Observable<void> {
    const formData = new FormData();
    formData.append('nombre', dto.nombre);
    formData.append('esPublica', String(dto.esPublica));
    if (dto.descripcion) formData.append('descripcion', dto.descripcion);
    if (dto.fotoPortada) formData.append('fotoPortada', dto.fotoPortada);
    return this.http.post<void>(`${this.baseURL}/playlists/crear`, formData);
  }

  /**
   * Obtiene una playlist desde el endpoint especificado.
   * @param endpoint URL completa del endpoint (ej: 'localhost:8080/api/playlists/{id}')
   */
  getPlaylist(endpoint: string): Observable<IPlaylistCompleta> {
    return this.http.get<IPlaylistCompleta>(this.baseURL + endpoint);
  }

  /**
   * Obtiene la lista de canciones favoritas del usuario autenticado.
   * GET /api/favoritos
   */
  getFavoritos(): Observable<IPista[]> {
    return this.http.get<IPista[] | null>(`${this.baseURL}/favoritos`).pipe(
      map(res => res ?? []),
      catchError(err => {
        // Si el status es 200 pero el body no es JSON válido (body vacío, texto plano, etc.)
        // devolvemos un array vacío para no romper la UI
        if (err?.status === 200) {
          console.warn('getFavoritos: respuesta 200 con body no parseable, se asume lista vacía.');
          return of([]);
        }
        throw err;
      }),
    );
  }

  /**
   * El usuario agrega una o varias canciones a una playlist existente.
   * El backend usa @RequestBody JSON.
   * POST /api/playlists/agregar-cancion
   */
  setAgregarCancionPlaylist(dto: ICancionesPlaylistRequest): Observable<void> {
    return this.http.post<void>(`${this.baseURL}/playlists/agregar-cancion`, dto);
  }

  /**
   * Añade una pista a la lista de favoritos del usuario autenticado.
   * POST /api/favoritos/{idPista}
   */
  addFavorito(idPista: number): Observable<void> {
    return this.http.post<void>(`${this.baseURL}/favoritos/${idPista}`, {});
  }

  /**
   * Elimina una pista de la lista de favoritos del usuario autenticado.
   * DELETE /api/favoritos/{idPista}
   */
  removeFavorito(idPista: number): Observable<void> {
    return this.http.delete<void>(`${this.baseURL}/favoritos/${idPista}`);
  }

  /**
   * Obtiene los IDs de las pistas favoritas del usuario autenticado.
   * GET /api/favoritos/ids
   */
  getFavoritosIds(): Observable<number[]> {
    return this.http.get<number[]>(`${this.baseURL}/favoritos/ids`);
  }

  /**
   * Obtiene las playlists del usuario autenticado.
   * GET /api/playlists/mis-playlists
   */
  getMisPlaylists(): Observable<IPlaylist[]> {
    return this.http.get<IPlaylist[]>(`${this.baseURL}/playlists/mis-playlists`);
  }

  /**
   * Busca playlists por nombre.
   * GET /api/playlists/buscar?q=término
   */
  buscarPlaylists(q: string): Observable<IPlaylist[]> {
    return this.http.get<IPlaylist[]>(`${this.baseURL}/playlists/buscar`, { params: { q } });
  }
}
