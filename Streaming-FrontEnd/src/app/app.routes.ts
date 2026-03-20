import { Routes } from '@angular/router';
import {authGuard} from './guards/authGuard';
import {publicGuard} from './guards/publicGuard';

export const routes: Routes = [
  {
    path: '',
    title: 'Inicio | KeySound',
    loadComponent: () =>
      import('./componentes/zonaPortal/pages/home/home')
        .then(m => m.Home),
  },

  {
    path: 'login',
    title: 'Inicio de sesión | KeySound',
    loadComponent: () =>
      import('./componentes/zonaCliente/Auth/login/login')
        .then(m => m.Login),
    canActivate: [publicGuard]
  },
  {
    path: 'register',
    title: 'Dale sonido a tu vida con KeySound | Regístrate ahora',
    loadComponent: () =>
      import('./componentes/zonaCliente/Auth/registro/registro')
        .then(m => m.Registro),
    canActivate: [publicGuard]
  },
  {
    path: '**',
    redirectTo: '',
    pathMatch: 'full',
  },
];
