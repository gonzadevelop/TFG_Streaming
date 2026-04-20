import {IUserLogin} from '../model/IUserLogin';
import {IUserRegister} from '../model/IUserRegister';
import {Injectable, inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../environments/environment';
import {Observable} from 'rxjs';
import {IAuthResponse, ICheckEmailResponse} from '../model/IAuth';

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

  resendVerificationEmail(email: string): Observable<void> {
    return this.http.post<void>(
      `${this.baseURL}/auth/resend-verification-email`,
      { email }
    );
  }
}
