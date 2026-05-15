import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  computed,
  inject,
  input,
  signal,
  WritableSignal,
} from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { TokenService } from '../../../services/tokenService';
import { SidebarService } from '../../../services/SidebarService';
import { UserService } from '../../../services/userService';

interface SidebarItem {
  label: string;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-sidebar-artista',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar-artista.html',
  styleUrl: './sidebar-artista.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SidebarArtista implements OnInit {
  private readonly tokenService = inject(TokenService);
  private readonly sidebarService = inject(SidebarService);
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);

  readonly isOpen = input<boolean>(true);
  userName: WritableSignal<string> = signal<string>('');
  avatarUrl: WritableSignal<string | null> = signal<string | null>(null);

  protected readonly inicialAvatar = computed<string>(() =>
    this.userName()[0]?.toUpperCase() ?? 'A'
  );

  readonly sidebarItems: SidebarItem[] = [
    { label: 'Panel artista', icon: 'dashboard', route: '/artista/home' },
    { label: 'Mis lanzamientos', icon: 'album', route: '/artista/albumes' },
    { label: 'Subir album', icon: 'upload', route: '/artista/subir' },
    { label: 'Perfil', icon: 'person', route: '/artista/perfil' },
  ];

  cerrarSesion(): void {
    this.tokenService.clearSession();
    this.userName.set('');
    this.avatarUrl.set(null);
    this.router.navigate(['/login']);
  }

  ngOnInit(): void {
    if (!this.tokenService.isLogged()) return;

    this.sidebarService.getUsername().subscribe({
      next: (username: string) => {
        this.userName.set(username);
        this.userService.getPerfilUsuario(username).subscribe({
          next: (perfil: { urlAvatar?: string | null }) => {
            if (perfil.urlAvatar && !perfil.urlAvatar.includes('ui-avatars')) {
              this.avatarUrl.set(perfil.urlAvatar);
            }
          },
          error: () => { /* avatar por defecto, sin accion */ },
        });
      },
      error: () => { /* nombre por defecto, sin accion */ },
    });
  }
}
