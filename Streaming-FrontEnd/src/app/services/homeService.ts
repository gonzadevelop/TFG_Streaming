import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import IUser from '../model/IUser';
import IPlaylist from '../model/IPlaylist';
import ICancion from '../model/ICancion';
import ILanzamiento from '../model/ILanzamiento';

@Injectable({ providedIn: 'root' })
export class HomeService {
  private readonly http = inject(HttpClient);
  private readonly baseURL = `${environment.apiURL}`;

  // ── Públicos (sin sesión) ***

  getPlaylistsDestacadas(): Observable<IPlaylist[]> {
    return this.http.get<IPlaylist[]>(`${this.baseURL}/playlists/KeySoundPlaylists`);
  }

  getArtistasPopulares(): Observable<IUser[]> {
    return this.http.get<IUser[]>(`${this.baseURL}/artistas/populares`);
  }

  getCancionesMasEscuchadas(): Observable<ICancion[]> {
    return this.http.get<ICancion[]>(`${this.baseURL}/canciones/mas-escuchadas`);
  }

  getProximosLanzamientos(): Observable<ILanzamiento[]> {
    return this.http.get<ILanzamiento[]>(`${this.baseURL}/lanzamientos/proximos`);
  }

  getNovedadesSemana(): Observable<ILanzamiento[]> {
    return this.http.get<ILanzamiento[]>(`${this.baseURL}/lanzamientos/novedades`);
  }

  // *** privados (con sesión de oyente) ***

  getMisPlaylists(): Observable<IPlaylist[]> {
    return this.http.get<IPlaylist[]>(`${this.baseURL}/usuario/playlists`);
  }

  // *** obtener la lista de artistas que sigo ***

  getArtistasQueSigo(): Observable<IUser[]> {
    return this.http.get<IUser[]>(`${this.baseURL}/usuario/artistas-seguidos`);
  }

  // *** obtener mis canciones más escuchadas ***

  getMisCancionesMasEscuchadas(): Observable<ICancion[]> {
    return this.http.get<ICancion[]>(`${this.baseURL}/canciones/mis-canciones-mas-reproducidas`);
  }

  // *** obtener mis próximos lanzamientos de los artistas que sigo ***

  getMisProximosLanzamientos(): Observable<ILanzamiento[]> {
    return this.http.get<ILanzamiento[]>(`${this.baseURL}/usuario/proximos-lanzamientos`);
  }

  // *** obtener mis novedades semanales de los artistas que sigo ***

}


