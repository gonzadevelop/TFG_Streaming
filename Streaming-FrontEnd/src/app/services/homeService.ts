import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {IHome} from '../model/home/IHome';

@Injectable({ providedIn: 'root' })
export class HomeService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  getDatosHome(): Observable<IHome> {
    return this.http.get<IHome>(`${this.baseUrl}/home/visualizar`);
  }
}
