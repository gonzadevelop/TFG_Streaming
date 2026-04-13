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
        path: 'configuracion',
        title: 'Configuracion | KeySound',
        loadComponent: () =>
          import('./componentes/zonaPortal/pages/settings/settings').then(m => m.SettingsComponent),
      },
      {
        path: 'mis-lanzamientos',
        title: 'Mis lanzamientos | KeySound',
        loadComponent: () =>
          import('./componentes/zonaPortal/pages/mis-lanzamientos/mis-lanzamientos').then(
            m => m.MisLanzamientosComponent
          ),
      },
      {
        path: 'estadisticas',
        title: 'Estadisticas | KeySound',
        loadComponent: () =>
          import('./componentes/zonaPortal/pages/estadisticas/estadisticas').then(
            m => m.EstadisticasComponent
          ),
      },
      {
        path: '',
        redirectTo: 'artista',
        pathMatch: 'full',
      },
    ],
  },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
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
    redirectTo: 'login',
  },
];
