import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../environments/environment';
import {Observable} from 'rxjs';
import { IPlaylistRequest, ICancionesPlaylistRequest } from '../model/playlists/IPlaylistRequest';

@Injectable({ providedIn: 'root' })
export class PlaylistService {
  private readonly http = inject(HttpClient);
  private readonly baseURL = `${environment.apiURL}`;

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
   * El usuario agrega una o varias canciones a una playlist existente.
   * El backend usa @RequestBody JSON.
   * POST /api/playlists/agregar-cancion
   */
  setAgregarCancionPlaylist(dto: ICancionesPlaylistRequest): Observable<void> {
    return this.http.post<void>(`${this.baseURL}/playlists/agregar-cancion`, dto);
  }
}
