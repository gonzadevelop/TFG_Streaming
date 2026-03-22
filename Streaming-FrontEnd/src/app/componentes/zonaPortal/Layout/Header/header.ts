import { Component, signal, ChangeDetectionStrategy } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NgOptimizedImage } from '@angular/common';
import { Footer } from './footer';

interface NavItem {
  route: string;
  label: string;
  icon?: string;
}

interface User {
  name: string;
  avatar: string;
}

@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive, NgOptimizedImage, Footer],
  templateUrl: './header.html',
  styleUrl: './header.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Header {

  readonly navItems: NavItem[] = [
    { route: '/header', label: 'Inicio', icon: 'home' },
    { route: '/login', label: 'Login', icon: 'login' },
    { route: '/register', label: 'Registro', icon: 'person_add' },
  ];

  readonly currentUser = signal<User>({
    name: 'Usuario',
    avatar: 'img/KeySound_logo_definitive_miniatura.png',
  });

  searchTerm = signal<string>('');

  openProfile(): void {
    // TODO: abrir panel de perfil
  }

  openSettings(): void {
    // TODO: abrir panel de ajustes
  }

  onSearch(): void {
    // TODO: implementar búsqueda con searchTerm()
  }

  clearSearch(): void {
    this.searchTerm.set('');
  }
}
