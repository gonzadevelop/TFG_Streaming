import { Routes } from '@angular/router';

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
    ],
  },
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
    path: 'verificar-email',
    title: 'Verifica tu correo | KeySound',
    loadComponent: () =>
      import('./componentes/zonaCliente/Auth/email-verification/email-verification')
        .then(m => m.EmailVerification),
  },
];
