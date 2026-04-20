import { ChangeDetectionStrategy, Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { TokenService } from '../../../../services/tokenService';

interface SidebarItem {
  label: string;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Sidebar implements OnInit {
  private readonly tokenService = inject(TokenService);

  readonly isOpen = input<boolean>(true);
  readonly userName = input<string>('Invitado');

  protected readonly estaLogueado = signal<boolean>(false);

  protected readonly inicialAvatar = computed<string>(() =>
    this.userName()[0]?.toUpperCase() ?? 'I'
  );

  protected readonly saludo = computed<string>(() => {
    const hora = 12;
    if (hora < 12) return '¡Buenos días';
    if (hora < 19) return '¡Buenas tardes';
    return '¡Buenas noches';
  });

  readonly sidebarItems: SidebarItem[] = [
    { label: 'Inicio', icon: 'home', route: '/home' },
    { label: 'Lista de favoritos', icon: 'equalizer', route: '/favs' },
    { label: 'Estadísticas', icon: 'insights', route: '/artista' },
    { label: 'Explorar', icon: 'search', route: '/artista' },
  ];

  ngOnInit(): void {
    this.estaLogueado.set(this.tokenService.isLogged());
  }
}


