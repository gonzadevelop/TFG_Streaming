import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { TokenService } from '../services/tokenService';

export const artistGuard: CanActivateFn = () => {
  const tokenService = inject(TokenService);
  const router = inject(Router);

  if (!tokenService.isLogged()) {
    return router.createUrlTree(['/login']);
  }

  const role = tokenService.getRole() ?? tokenService.getRolesFromToken()[0] ?? null;
  if (role === 'ROLE_ARTISTA') {
    return true;
  }

  return router.createUrlTree(['/']);
};
