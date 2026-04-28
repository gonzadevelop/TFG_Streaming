import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class SidebarService {
  private readonly http = inject(HttpClient);
  private readonly baseURL = `${environment.apiURL}`;

  getUsername(): Observable<string> {
    return this.http.get(`${this.baseURL}/usuarios/obtener-username`, {
      responseType: 'text'
    });
  }
}
