import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { TokenService } from '../services/tokenService';

export const nonArtistGuard: CanActivateFn = () => {
  const tokenService = inject(TokenService);
  const router = inject(Router);

  if (!tokenService.isLogged()) return true;

  const role = tokenService.getPrimaryRole();
  if (role === 'ROLE_ARTISTA') {
    return router.createUrlTree(['/artista/home']);
  }

  return true;
};

