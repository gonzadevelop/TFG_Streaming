import { Routes } from '@angular/router';
import { publicGuard } from './guards/publicGuard';
import { artistGuard } from './guards/artistGuard';
import { nonArtistGuard } from './guards/nonArtistGuard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./componentes/zonaPortal/Layout/layout').then(m => m.Layout),
    canActivate: [nonArtistGuard],
    children: [
      {
        path: '',
        title: 'Abre todas las puertas a tus oídos | KeySound',
        loadComponent: () =>
          import('./componentes/zonaPortal/pages/cliente/home/home').then(m => m.Home),
      },
      {
        path: 'playlists',
        children: [
          {
            path: 'keysound',
            title: 'KeySound | Playlists',
            loadComponent: () => import('./componentes/zonaPortal/pages/cliente/keysound-playlists/keysound-playlists')
              .then(m => m.Playlists),
          },
          {
            path: ':id',
            title: 'Playlist | KeySound',
            loadComponent: () => import('./componentes/zonaPortal/pages/cliente/playlist/playlist')
              .then(m => m.Playlist),
          }
        ],
      },
      {
        path: 'favs',
        title: 'Mis canciones favs :) | KeySound',
        loadComponent: () => import('./componentes/zonaPortal/pages/cliente/lista-favoritos/lista-favoritos')
          .then(m => m.ListaFavoritos),
      },
      {
        path: 'artistas/:username',
        title: 'Artista | KeySound',
        loadComponent: () => import('./componentes/zonaPortal/pages/cliente/artista/artista')
          .then(m => m.Artista),
      },
      {
        path: 'album/:id',
        title: 'Album | KeySound',
        loadComponent: () => import('./componentes/zonaPortal/pages/cliente/album/album')
          .then(m => m.Album),
      },
      {
        path: 'perfil',
        title: 'Mi perfil | KeySound',
        loadComponent: () => import('./componentes/zonaPortal/pages/cliente/perfil/perfil')
          .then(m => m.Perfil),
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
    path: 'artista',
    canActivate: [artistGuard],
    loadComponent: () =>
      import('./componentes/zonaPortal/LayoutArtista/layout-artista').then(m => m.LayoutArtista),
    children: [
      {
        path: 'home',
        title: 'Panel de artista | KeySound',
        loadComponent: () =>
          import('./componentes/zonaPortal/pages/artista/artista-home/artista-home')
            .then(m => m.ArtistaHome),
      },
      {
        path: 'albumes',
        title: 'Mis lanzamientos | KeySound',
        loadComponent: () =>
          import('./componentes/zonaPortal/pages/artista/artista-albums/artista-albums')
            .then(m => m.ArtistaAlbums),
      },
      {
        path: 'subir',
        title: 'Subir álbum | KeySound',
        loadComponent: () =>
          import('./componentes/zonaPortal/pages/artista/artista-subir-album/artista-subir-album')
            .then(m => m.ArtistaSubirAlbum),
      },
      {
        path: 'perfil',
        title: 'Perfil de artista | KeySound',
        loadComponent: () => import('./componentes/zonaPortal/pages/cliente/perfil/perfil')
          .then(m => m.Perfil),
      },
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'home',
      },
    ],
  },
  {
    path: '**',
    redirectTo: '',
  },
];
