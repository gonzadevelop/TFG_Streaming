import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../environments/environment';
import {Observable} from 'rxjs';
import { IArtista } from '../model/artista/IArtista';

@Injectable({ providedIn: 'root' })
export class ArtistaService {
  private readonly http = inject(HttpClient);
  private readonly baseURL = `${environment.apiURL}`;

  /**
   * Obtiene la información completa de un artista.
   * GET /api/artistas/{username}
   */
  getArtista(username: string): Observable<IArtista> {
    return this.http.get<IArtista>(`${this.baseURL}/artistas/${username}`);
  }
}

