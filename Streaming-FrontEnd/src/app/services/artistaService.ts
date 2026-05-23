import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../environments/environment';
import {Observable} from 'rxjs';
import { IArtista } from '../model/artista/IArtista';
import { IArtistaHome } from '../model/home/IArtistaHome';
import { IMiAlbum } from '../model/album/IMiAlbum';

@Injectable({ providedIn: 'root' })
export class ArtistaService {
  private readonly http = inject(HttpClient);
  private readonly baseURL = `${environment.apiURL}`;

  getArtista(username: string): Observable<IArtista> {
    return this.http.get<IArtista>(`${this.baseURL}/artistas/${username}`);
  }

  getMisAlbums(): Observable<IMiAlbum[]> {
    return this.http.get<IMiAlbum[]>(`${this.baseURL}/artistas/mis-albums`);
  }

  publicarAlbum(albumId: number): Observable<void> {
    return this.http.patch<void>(`${this.baseURL}/artistas/publicar/${albumId}`, {});
  }

  eliminarAlbum(albumId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseURL}/artistas/eliminar/${albumId}`);
  }

  getArtistasQueSigo(): Observable<IArtistaHome[]> {
    return this.http.get<IArtistaHome[]>(`${this.baseURL}/artistas/artistas-que-sigo`);
  }

  buscarArtistas(q: string): Observable<IArtistaHome[]> {
    return this.http.get<IArtistaHome[]>(`${this.baseURL}/artistas/buscar`, { params: { q } });
  }
}
