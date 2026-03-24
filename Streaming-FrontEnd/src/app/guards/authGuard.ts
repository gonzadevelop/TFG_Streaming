import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {TokenService} from '../services/tokenService';

export const authGuard: CanActivateFn = () => {
  const token = inject(TokenService)
  const router = inject(Router);

  if (token.isLogged()) {
    return true;
  }

  return router.createUrlTree(
    ['/login']
  )
}
