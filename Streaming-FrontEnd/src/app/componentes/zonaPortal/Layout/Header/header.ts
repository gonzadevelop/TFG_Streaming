import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { NgOptimizedImage } from '@angular/common';
import { Footer } from '../../../zonaFooter/footer';

interface NavItem {
  route: string;
  label: string;
  icon?: string;
}

interface User {
  name: string;
  avatar: string;
}

interface SidebarItem {
  label: string;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive, RouterOutlet, NgOptimizedImage, Footer],
  templateUrl: './header.html',
  styleUrl: './header.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Header {
  readonly isSidebarOpen = signal(true);
  readonly isUserMenuOpen = signal(false);

  readonly navItems: NavItem[] = [
    { route: '/header/artista', label: 'Inicio', icon: 'home' },
    { route: '/header/artista', label: 'Playlist KeySound', icon: 'library_music' },
    { route: '/header/artista', label: 'Podcast', icon: 'podcasts' },
  ];

  readonly sidebarItems: SidebarItem[] = [
    { label: 'Panel del artista', icon: 'equalizer', route: '/header/artista' },
    { label: 'Mis lanzamientos', icon: 'album', route: '/header/artista' },
    { label: 'Estadisticas', icon: 'insights', route: '/header/artista' },
  ];

  readonly currentUser = signal<User>({
    name: this.resolveUserName(),
    avatar: 'img/KeySound_logo_definitive_miniatura.png',
  });

  readonly greetingMessage = computed(() => {
    const hour = new Date().getHours();
    if (hour < 12) {
      return 'Buenos dias';
    }
    if (hour < 20) {
      return 'Buenas tardes';
    }
    return 'Buenas noches';
  });

  readonly searchTerm = signal<string>('');

  toggleSidebar(): void {
    this.isSidebarOpen.update((value) => !value);
    if (!this.isSidebarOpen()) {
      this.isUserMenuOpen.set(false);
    }
  }

  toggleUserMenu(): void {
    this.isUserMenuOpen.update((value) => !value);
  }

  openSettings(): void {
    this.isUserMenuOpen.set(true);
  }

  onSearch(): void {
    // TODO: implementar búsqueda con searchTerm()
  }

  clearSearch(): void {
    this.searchTerm.set('');
  }

  private resolveUserName(): string {
    if (typeof window === 'undefined') {
      return 'Artista';
    }

    const keys = ['userName', 'username', 'name'];
    for (const key of keys) {
      const value = window.localStorage.getItem(key);
      if (value && value.trim()) {
        return value.trim();
      }
    }

    return 'Artista';
  }
}
