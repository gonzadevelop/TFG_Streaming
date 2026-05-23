import { Routes } from '@angular/router';
import { publicGuard } from './guards/publicGuard';
import { artistGuard } from './guards/artistGuard';
import { nonArtistGuard } from './guards/nonArtistGuard';
import { authGuard } from './guards/authGuard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./componentes/zonaPortal/Layout/cliente/layout').then(m => m.Layout),
    canActivate: [nonArtistGuard],
    children: [
      {
        path: '',
        title: 'Abre todas las puertas a tus oídos | KeySound',
        canActivate: [authGuard],
        loadComponent: () =>
          import('./componentes/zonaPortal/pages/cliente/home/home').then(m => m.Home),
      },
      {
        path: 'playlists',
        children: [
          {
            path: 'keysound',
            title: 'KeySound | Playlists',
            canActivate: [authGuard],
            loadComponent: () => import('./componentes/zonaPortal/pages/cliente/keysound-playlists/keysound-playlists')
              .then(m => m.Playlists),
          },
          {
            path: ':id',
            title: 'Playlist | KeySound',
            canActivate: [authGuard],
            loadComponent: () => import('./componentes/zonaPortal/pages/cliente/playlist/playlist')
              .then(m => m.Playlist),
          }
        ],
      },
      {
        path: 'favs',
        title: 'Mis canciones favs :) | KeySound',
        canActivate: [authGuard],
        loadComponent: () => import('./componentes/zonaPortal/pages/cliente/lista-favoritos/lista-favoritos')
          .then(m => m.ListaFavoritos),
      },
      {
        path: 'artistas/:username',
        title: 'Artista | KeySound',
        canActivate: [authGuard],
        loadComponent: () => import('./componentes/zonaPortal/pages/cliente/artista/artista')
          .then(m => m.Artista),
      },
      {
        path: 'album/:id',
        title: 'Album | KeySound',
        canActivate: [authGuard],
        loadComponent: () => import('./componentes/zonaPortal/pages/cliente/album/album')
          .then(m => m.Album),
      },
      {
        path: 'mis-playlists',
        title: 'Mis playlists | KeySound',
        canActivate: [authGuard],
        loadComponent: () => import('./componentes/zonaPortal/pages/cliente/mis-playlists/mis-playlists')
          .then(m => m.MisPlaylists),
      },
      {
        path: 'mis-artistas',
        title: 'Artistas que sigues | KeySound',
        canActivate: [authGuard],
        loadComponent: () => import('./componentes/zonaPortal/pages/cliente/mis-artistas/mis-artistas')
          .then(m => m.MisArtistas),
      },
      {
        path: 'perfil',
        title: 'Mi perfil | KeySound',
        canActivate: [authGuard],
        loadComponent: () => import('./componentes/zonaPortal/pages/cliente/perfil/perfil')
          .then(m => m.Perfil),
      },
      {
        path: 'stadistics',
        title: 'Mis estadísticas | KeySound',
        canActivate: [authGuard],
        loadComponent: () => import('./componentes/zonaPortal/pages/cliente/estadistica/estadistica')
          .then(m => m.Estadistica),
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
    path: 'artista',
    canActivate: [artistGuard],
    loadComponent: () =>
      import('./componentes/zonaPortal/Layout/artista/layout-artista').then(m => m.LayoutArtista),
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
