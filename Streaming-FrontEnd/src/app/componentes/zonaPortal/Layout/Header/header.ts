import { Component, signal, ChangeDetectionStrategy } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NgOptimizedImage } from '@angular/common';

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
  imports: [RouterLink, RouterLinkActive, NgOptimizedImage],
  templateUrl: './header.html',
  styleUrl: './header.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Header {

  readonly navItems: NavItem[] = [
    { route: '/inicio', label: 'Inicio', icon: 'home' },
    { route: '/explorar', label: 'Explorar', icon: 'explore' },
    { route: '/biblioteca', label: 'Mi Biblioteca', icon: 'library_music' },
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