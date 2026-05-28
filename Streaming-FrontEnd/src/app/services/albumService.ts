import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import { Observable } from 'rxjs';
import { IAlbumCompleto } from '../model/album/IAlbumCompleto';
import { IAlbum } from '../model/album/IAlbum';
import { IAlbumUpload } from '../model/album/IAlbumUpload';

@Injectable ({ providedIn: 'root' })
export class AlbumService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  getAlbum(albumId: number): Observable<IAlbumCompleto> {
    return this.http.get<IAlbumCompleto>(`${this.baseUrl}/albums/${albumId}`);
  }

  getNovedades(): Observable<IAlbum[]> {
    return this.http.get<IAlbum[]>(`${this.baseUrl}/albums/novedades`);
  }

  getProximosLanzamientos(): Observable<IAlbum[]> {
    return this.http.get<IAlbum[]>(`${this.baseUrl}/albums/proximos`);
  }

  buscarAlbums(q: string): Observable<IAlbum[]> {
    return this.http.get<IAlbum[]>(`${this.baseUrl}/albums/buscar`, { params: { q } });
  }

  subirAlbum(dto: IAlbumUpload, portada: File, archivos: File[]): Observable<void> {
    const form = new FormData();
    form.append('datos', new Blob([JSON.stringify(dto)], { type: 'application/json' }));
    form.append('portada', portada);
    archivos.forEach((archivo) => form.append('archivos', archivo));

    return this.http.post<void>(`${this.baseUrl}/albums/crear`, form);
  }
}
