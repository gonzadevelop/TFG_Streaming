import {IUserLogin} from '../model/auth/IUserLogin';
import {IUserRegister} from '../model/auth/IUserRegister';
import {Injectable, inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../environments/environment';
import {Observable} from 'rxjs';
import {IAuthResponse, ICheckEmailResponse} from '../model/auth/IAuth';

@Injectable({
  providedIn: 'root'
})

export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly baseURL = `${environment.apiURL}`;

  login(credentials: IUserLogin): Observable<IAuthResponse> {
    return this.http.post<IAuthResponse>(
      `${this.baseURL}/auth/login`,
      credentials);
  }

  register(userData: IUserRegister): Observable<IAuthResponse> {
    return this.http.post<IAuthResponse>(
      `${this.baseURL}/auth/register`,
      userData);
  }

  checkEmail(email: string): Observable<ICheckEmailResponse> {
    return this.http.post<ICheckEmailResponse>(
      `${this.baseURL}/auth/check-email`,
      { email });
  }

}
