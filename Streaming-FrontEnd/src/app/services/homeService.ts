import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import IUser from '../model/IUser';
import IPlaylist from '../model/IPlaylist';
import ICancion from '../model/ICancion';
import ILanzamiento from '../model/ILanzamiento';
import {IHome} from '../model/home/IHome';

@Injectable({ providedIn: 'root' })
export class HomeService {
  private readonly http = inject(HttpClient);
  private readonly baseURL = `${environment.apiURL}`;

  getDatosHome(): Observable<IHome> {
    return this.http.get<IHome>(`${this.baseURL}/home/visualizar`);
  }
}


