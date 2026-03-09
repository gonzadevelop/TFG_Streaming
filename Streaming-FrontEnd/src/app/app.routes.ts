import { Routes } from '@angular/router';


export const routes: Routes = [
  {
    path: '',
    title: 'KeySound - Tu mundo musical, a tu alcance.',
    loadComponent: () =>
      import('./componentes/zonaCliente/Auth/login/login').then(m => m.Login),
  },
  {
    path: 'login',
    title: 'Inicio de sesión - KeySound',
    loadComponent: () =>
      import('./componentes/zonaCliente/Auth/login/login').then(m => m.Login),
  },
  {
    path: 'registro',
    title: 'Registrarse - KeySound',
    loadComponent: () =>
      import('./componentes/zonaCliente/Auth/registro/registro').then(m => m.Registro),
  },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },
];
