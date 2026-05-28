import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import { IArtista } from '../model/artista/IArtista';
import { IArtistaHome } from '../model/home/IArtistaHome';
import { IMiAlbum } from '../model/album/IMiAlbum';

@Injectable({ providedIn: 'root' })
export class ArtistaService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  getArtista(username: string): Observable<IArtista> {
    return this.http.get<IArtista>(`${this.baseUrl}/artistas/${username}`);
  }

  getMisAlbums(): Observable<IMiAlbum[]> {
    return this.http.get<IMiAlbum[]>(`${this.baseUrl}/artistas/mis-albums`);
  }

  publicarAlbum(albumId: number): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/artistas/publicar/${albumId}`, {});
  }

  eliminarAlbum(albumId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/artistas/eliminar/${albumId}`);
  }

  getArtistasQueSigo(): Observable<IArtistaHome[]> {
    return this.http.get<IArtistaHome[]>(`${this.baseUrl}/artistas/artistas-que-sigo`);
  }

  buscarArtistas(q: string): Observable<IArtistaHome[]> {
    return this.http.get<IArtistaHome[]>(`${this.baseUrl}/artistas/buscar`, { params: { q } });
  }
}
