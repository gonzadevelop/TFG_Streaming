import {Injectable, inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../environments/environment';
import {Observable} from 'rxjs';
import IUser from '../model/IUser';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly baseURL = `${environment.apiURL}/users`

  getAll(): Observable<IUser[]> {
    return this.http.get<IUser[]>(this.baseURL);;
  }

  getById(id: number): Observable<IUser> {
    return this.http.get<IUser>(`${this.baseURL}/${id}`);;
  }
}
