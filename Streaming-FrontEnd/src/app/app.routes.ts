import { Routes } from '@angular/router';
import {publicGuard} from './guards/publicGuard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./componentes/zonaPortal/Layout/layout').then(m => m.Layout),
    children: [
      {
        path: '',
        title: 'Abre todas las puertas a tus oídos | KeySound',
        loadComponent: () =>
          import('./componentes/zonaPortal/pages/home/home').then(m => m.Home),
      },
      {
        path: 'playlists',
        children: [
          {
            path: 'keysound',
            title: 'KeySound | Playlists',
            loadComponent: () => import('./componentes/zonaPortal/pages/keysound-playlists/keysound-playlists')
              .then(m => m.Playlists),
          },
          {
            path: ':id',
            title: 'Playlist | KeySound',
            loadComponent: () => import('./componentes/zonaPortal/pages/playlist/playlist')
              .then(m => m.Playlist),
          }
        ],
      },
      {
        path: 'artistas/:username',
        title: 'Artista | KeySound',
        loadComponent: () => import('./componentes/zonaPortal/pages/artista/artista')
          .then(m => m.Artista),
      },
      {
        path: 'album/:id',
        title: 'Album | KeySound',
        loadComponent: () => import('./componentes/zonaPortal/pages/album/album')
          .then(m => m.Album),
      },
    ],
  },
  {
    path: 'login',
    title: 'Inicio de sesión | KeySound',
    canActivate: [publicGuard],
    loadComponent: () =>
      import('./componentes/zonaCliente/Auth/login/login').then(m => m.Login),
  },
  {
    path: 'register',
    title: 'Dale sonido a tu vida con KeySound | Regístrate ahora',
    canActivate: [publicGuard],
    loadComponent: () =>
      import('./componentes/zonaCliente/Auth/registro/registro').then(m => m.Registro),
  },
  {
    path: 'verificar-email',
    title: 'Verifica tu correo | KeySound',
    loadComponent: () =>
      import('./componentes/zonaCliente/Auth/email-verification/email-verification')
        .then(m => m.EmailVerification),
  },
  {
    path: '**',
    redirectTo: '',
  }
];
