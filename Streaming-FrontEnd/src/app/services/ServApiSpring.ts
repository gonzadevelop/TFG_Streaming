import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { inject } from '@angular/core';
import ILoginRequest from '../model/ILoginRequest';
import ILoginResponse from '../model/ILoginResponse';

@Injectable({
  providedIn: 'root'
})
export class ServApiSpring {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api';

  login(request: ILoginRequest): Observable<HttpResponse<ILoginResponse>> {
    return this.http.post<ILoginResponse>(
      `${this.apiUrl}/auth/login`,
      request,
      { observe: 'response' }
    );
  }

  checkResitro(email: string): Observable<HttpResponse<boolean>> {
    return this.http.post<boolean>(
      `${this.apiUrl}/auth/check-email`,
      { email },
      { observe: 'response' }
    );
  }
}
