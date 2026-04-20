import {Injectable, inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../environments/environment';
import {Observable} from 'rxjs';
import IUser from '../model/IUser';
import IFavorito from '../model/IFavorito';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly baseURL = `${environment.apiURL}/usuarios`;

  getAll(): Observable<IUser[]> {
    return this.http.get<IUser[]>(this.baseURL);
  }

  getById(id: number): Observable<IUser> {
    return this.http.get<IUser>(`${this.baseURL}/${id}`);
  }

  // *** obtener la información de un usuario por su ID ***
  getInfoUsuario(id: number): Observable<IUser> {
    return this.http.get<IUser>(`${this.baseURL}/${id}/info`);
  }

  // *** agregar una canción a favoritos ***
  agregarFavorito(usuarioId: number, cancionId: number): Observable<IFavorito> {
    return this.http.post<IFavorito>(`${this.baseURL}/agregar-cancion-favoritos/${usuarioId}`, { cancionId });
  }

  // *** eliminar una canción de favoritos ***
  eliminarFavorito(usuarioId: number, cancionId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseURL}/${usuarioId}/favoritos/${cancionId}`);
  }

  // Obtener canciones favoritas de un usuario
  getFavoritos(usuarioId: number): Observable<IFavorito[]> {
    return this.http.get<IFavorito[]>(`${this.baseURL}/${usuarioId}/favoritos`);
  }

  // *** seguir a otro usuario ***
  seguirUsuario(usuarioId: number, seguidoId: number): Observable<void> {
    return this.http.post<void>(`${this.baseURL}/seguir`, {});
  }

  // *** dejar de seguir a otro usuario ***
  dejarDeSeguirUsuario(usuarioId: number, seguidoId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseURL}/dejar-seguir/`);
  }

  // *** obtener lista de usuarios que sigue un usuario ***
  getSeguidores(usuarioId: number): Observable<IUser[]> {
    return this.http.get<IUser[]>(`${this.baseURL}/${usuarioId}/siguiendo`);
  }
}
