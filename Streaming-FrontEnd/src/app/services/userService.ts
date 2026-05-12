import {Injectable, inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../environments/environment';
import {Observable, EMPTY} from 'rxjs';
import {catchError} from 'rxjs/operators';
import IUser from '../model/IUser';
import IFavorito from '../model/IFavorito';
import {IResponseUsuario, IUpdatePerfilRequest} from '../model/IResponseUsuario';

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

  // *** seguir a un artista (envía id como cuerpo raw) ***
  seguirArtista(artistaId: number): Observable<void> {
    return this.http.post<void>(`${this.baseURL}/seguir`, artistaId);
  }

  // *** dejar de seguir a un artista ***
  dejarDeSeguirArtista(artistaId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseURL}/dejar-seguir/${artistaId}`);
  }

  // *** obtener lista de usuarios que sigue un usuario ***
  getSeguidores(usuarioId: number): Observable<IUser[]> {
    return this.http.get<IUser[]>(`${this.baseURL}/${usuarioId}/siguiendo`);
  }

  // *** obtener info completa del perfil por username ***
  getPerfilUsuario(username: string): Observable<IResponseUsuario> {
    return this.http.get<IResponseUsuario>(`${this.baseURL}/${username}`);
  }

  // *** actualizar datos de perfil (biografia, email) ***
  actualizarPerfil(datos: IUpdatePerfilRequest): Observable<IResponseUsuario> {
    return this.http.put<IResponseUsuario>(`${this.baseURL}/perfil`, datos);
  }

  // *** actualizar avatar (multipart) ***
  actualizarAvatar(file: File): Observable<string> {
    const form = new FormData();
    form.append('avatar', file);
    return this.http.post(`${this.baseURL}/avatar`, form, { responseType: 'text' });
  }
}
