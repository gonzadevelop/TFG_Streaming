import { ChangeDetectionStrategy, Component, computed, output, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NgOptimizedImage } from '@angular/common';

@Component({
  selector: 'app-header',
  imports: [RouterLink, NgOptimizedImage],
  templateUrl: './header.html',
  styleUrl: './header.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Header {
  readonly isSidebarOpen = signal(true);
  readonly isUserMenuOpen = signal(false);

  readonly sidebarToggled = output<boolean>();

  readonly greetingMessage = computed(() => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Buenos días';
    if (hour < 20) return 'Buenas tardes';
    return 'Buenas noches';
  });

  readonly searchTerm = signal<string>('');

  toggleSidebar(): void {
    this.isSidebarOpen.update((v) => !v);
    this.sidebarToggled.emit(this.isSidebarOpen());
  }

  toggleUserMenu(): void {
    this.isUserMenuOpen.update((v) => !v);
  }

  openSettings(): void {
    this.isUserMenuOpen.set(true);
  }

  onSearch(): void {}

  clearSearch(): void {
    this.searchTerm.set('');
  }
}

