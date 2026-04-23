import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { IsActiveMatchOptions, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { NgOptimizedImage } from '@angular/common';
import { Footer } from '../../../zonaFooter/footer';

interface NavItem {
  route: string;
  label: string;
  icon?: string;
  queryParams?: Record<string, string>;
}

interface User {
  name: string;
  avatar: string;
}

interface SidebarItem {
  label: string;
  icon: string;
  route: string;
  queryParams?: Record<string, string>;
}

@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive, RouterOutlet, NgOptimizedImage, Footer],
  templateUrl: './header.html',
  styleUrl: './header.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Header {
  private readonly router = inject(Router);
  readonly exactRouteMatch: IsActiveMatchOptions = {
    paths: 'exact',
    queryParams: 'exact',
    matrixParams: 'ignored',
    fragment: 'ignored',
  };

  readonly isSidebarOpen = signal(true);

  readonly navItems: NavItem[] = [
    { route: '/header/artista', label: 'Inicio', icon: 'home' },
    { route: '/header/mis-lanzamientos', label: 'Mis lanzamientos', icon: 'library_music' },
    { route: '/header/estadisticas', label: 'Estadisticas', icon: 'podcasts' },
  ];

  readonly sidebarItems: SidebarItem[] = [
    { label: 'Panel principal', icon: 'equalizer', route: '/header/artista' },
    { label: 'Mis lanzamientos', icon: 'album', route: '/header/mis-lanzamientos' },
    { label: 'Estadisticas', icon: 'insights', route: '/header/estadisticas' },
    { label: 'Configuracion', icon: 'settings', route: '/header/configuracion', queryParams: { section: 'profile' } },
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
  }

  goToAccountSettings(): void {
    this.router.navigate(['/header/configuracion'], {
      queryParams: { section: 'account' },
    });
  }

  onSearch(): void {
    const term = this.searchTerm().trim();

    if (!term) {
      this.router.navigate(['/header/artista']);
      return;
    }

    if (typeof window !== 'undefined') {
      window.localStorage.setItem('lastSearch', term);
    }

    this.router.navigate(['/header/artista'], {
      queryParams: { q: term },
    });
  }

  clearSearch(): void {
    this.searchTerm.set('');
  }

  private resolveUserName(): string {
    if (typeof window === 'undefined') {
      return 'Usuario';
    }

    const keys = ['userName', 'username', 'name'];
    for (const key of keys) {
      const value = window.localStorage.getItem(key);
      if (value && value.trim()) {
        return value.trim();
      }
    }

    return 'Usuario';
  }
}
