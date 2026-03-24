import {HttpInterceptorFn} from '@angular/common/http';
import {inject} from '@angular/core';
import {TokenService} from '../services/tokenService';

export const authInterceptor: HttpInterceptorFn = (req, res) => {
  const tokenService = inject(TokenService);
  const token = tokenService.getToken();

  if (token) {
    const cloned = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    return res(cloned);
  }

  return res(req);
}
