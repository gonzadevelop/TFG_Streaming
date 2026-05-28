import { Injectable, inject, signal, WritableSignal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class SidebarService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  /** Señal compartida para el avatar: la actualiza perfil.ts y la leen los sidebars */
  readonly avatarUrl: WritableSignal<string | null> = signal<string | null>(null);

  getUsername(): Observable<string> {
    return this.http.get(`${this.baseUrl}/usuarios/obtener-username`, {
      responseType: 'text'
    });
  }
}
