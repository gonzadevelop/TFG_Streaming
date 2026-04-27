import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../environments/environment';
import { Observable } from 'rxjs';
import { IAlbumCompleto } from '../model/album/IAlbumCompleto';

@Injectable ({ providedIn: 'root' })
export class AlbumService {
  private readonly http = inject(HttpClient);
  private readonly baseURL = `${environment.apiURL}`;

  /**
   * Obtiene un album completo desde el endpoint.
   * GET /api/albums/{albumId}
   * @param albumId ID del album
   */
  getAlbum(albumId: number): Observable<IAlbumCompleto> {
    return this.http.get<IAlbumCompleto>(`${this.baseURL}/albums/${albumId}`);
  }
}
