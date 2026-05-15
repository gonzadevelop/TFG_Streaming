import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  computed,
  inject,
  input,
  signal,
  WritableSignal
} from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { TokenService } from '../../../../services/tokenService';
import {SidebarService} from '../../../../services/SidebarService';
import {Router} from '@angular/router';
import {UserService} from '../../../../services/userService';

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
  private readonly sidebarService = inject(SidebarService);
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);

  readonly isOpen = input<boolean>(true);
  userName: WritableSignal<string> = signal<string>('');
  avatarUrl: WritableSignal<string | null> = signal<string | null>(null);
  role: WritableSignal<string | null> = signal<string | null>(null);

  protected readonly dropdownOpen = signal<boolean>(false);
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
    {label: 'Inicio', icon: 'home', route: '/'},
    {label: 'Lista de favoritos', icon: 'favorite', route: '/favs'},
    {label: 'Estadísticas', icon: 'insights', route: '/stadistics'},
    {label: 'Explorar', icon: 'search', route: '/explore'},
  ];

  toggleDropdown(): void {
    this.dropdownOpen.update(v => !v);
  }

  closeDropdown(): void {
    this.dropdownOpen.set(false);
  }

  cerrarSesion(): void {
    this.tokenService.clearSession();
    this.estaLogueado.set(false);
    this.userName.set('');
    this.avatarUrl.set(null);
    this.role.set(null);
    this.closeDropdown();
    this.router.navigate(['/']);
  }

  ngOnInit(): void {
    this.estaLogueado.set(this.tokenService.isLogged());
    if (this.estaLogueado()) {
      this.role.set(this.tokenService.getPrimaryRole());
      this.sidebarService.getUsername().subscribe({
        next: (username) => {
          this.userName.set(username);
          this.userService.getPerfilUsuario(username).subscribe({
            next: (perfil) => {
              if (perfil.urlAvatar && !perfil.urlAvatar.includes('ui-avatars')) {
                this.avatarUrl.set(perfil.urlAvatar);
              }
            },
            error: () => { /* avatar por defecto, sin acción */ }
          });
        },
        error: (err) => {
          console.error('Error al obtener el nombre de usuario:', err);
        }
      });
    }
  }
}
