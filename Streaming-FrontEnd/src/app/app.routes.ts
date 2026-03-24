import { Routes } from '@angular/router';

export const routes: Routes = [

  {
    path: 'login',
    title: 'Inicio de sesión | KeySound',
    loadComponent: () =>
      import('./componentes/zonaCliente/Auth/login/login').then(m => m.Login),
  },
  {
    path: 'register',
    title: 'Dale sonido a tu vida con KeySound | Regístrate ahora',
    loadComponent: () =>
      import('./componentes/zonaCliente/Auth/registro/registro').then(m => m.Registro),
  },
  {
    path: 'header',
    title: 'Header | KeySound',
    loadComponent: () =>
      import('./componentes/zonaPortal/Layout/Header/header').then(m => m.Header),
    children: [
      {
        path: 'artista',
        title: 'Zona Artista | KeySound',
        loadComponent: () =>
          import('./componentes/zonaArtista/artista').then(m => m.Artista),
      },
      {
        path: '',
        redirectTo: 'artista',
        pathMatch: 'full',
      },
    ],
  },
  {
    path: 'artista',
    redirectTo: 'header/artista',
    pathMatch: 'full',
  },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },
  {
    path: '**',
    redirectTo: 'header/artista',
  },
];
