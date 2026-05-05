import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../environments/environment';
import {Observable} from 'rxjs';
import { IArtista } from '../model/artista/IArtista';
import { IArtistaHome } from '../model/home/IArtistaHome';

@Injectable({ providedIn: 'root' })
export class ArtistaService {
  private readonly http = inject(HttpClient);
  private readonly baseURL = `${environment.apiURL}`;

  getArtista(username: string): Observable<IArtista> {
    return this.http.get<IArtista>(`${this.baseURL}/artistas/${username}`);
  }

  buscarArtistas(q: string): Observable<IArtistaHome[]> {
    return this.http.get<IArtistaHome[]>(`${this.baseURL}/artistas/buscar`, { params: { q } });
  }
}

