import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './Header/header';
import { Footer } from './Footer/footer';
import { Sidebar } from './Sidebar/sidebar';
import { Player } from './Player/player';
import { FavoritosService } from '../../../../services/favoritosService';

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet, HeaderComponent, Footer, Sidebar, Player],
  styleUrl: './layout.css',
  template: `
    <app-header />

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
export class Layout implements OnInit {
  private readonly favoritosService = inject(FavoritosService);

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

  ngOnInit(): void {
    this.favoritosService.cargarFavoritos();
  }
}
