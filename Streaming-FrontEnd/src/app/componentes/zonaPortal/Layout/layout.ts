import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from './Header/header';
import { Footer } from './Footer/footer';
import { Sidebar } from './Sidebar/sidebar';
import { Player } from './Player/player';

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet, Header, Footer, Sidebar, Player],
  styleUrl: './layout.css',
  template: `
    <app-header (sidebarToggled)="sidebarOpen.set($event)" />

    <div class="layout-shell" [class.layout-shell--collapsed]="!sidebarOpen()">
      <app-sidebar
        [hidden]="!sidebarOpen()"
        [isOpen]="sidebarOpen()"
      />

      <main class="layout-content" id="main-content">
        <router-outlet />
      </main>
    </div>
    <app-footer />
    <app-player />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Layout {
  readonly sidebarOpen = signal(true);

  readonly userName = computed(() => {
    if (typeof window === 'undefined') return 'Usuario';
    const keys = ['userName', 'username', 'name'];
    for (const key of keys) {
      const value = window.localStorage.getItem(key);
      if (value?.trim()) return value.trim();
    }
    return 'Usuario';
  });
}
