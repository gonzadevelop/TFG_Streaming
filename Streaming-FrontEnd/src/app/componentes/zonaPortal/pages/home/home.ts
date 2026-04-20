import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import { RouterLink } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { TokenService } from '../../../../services/tokenService';
import { HomeService } from '../../../../services/homeService';
import IPlaylist from '../../../../model/IPlaylist';
import IUser from '../../../../model/IUser';
import ICancion from '../../../../model/ICancion';
import ILanzamiento from '../../../../model/ILanzamiento';
import {IAlbum} from '../../../../model/IAlbum';

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Home implements OnInit {

  private readonly tokenService = inject(TokenService);
  private readonly homeService = inject(HomeService);

  // ── Estado de sesión ────────────────────────────────────
  protected readonly estaLogueado = signal<boolean>(false);
  protected readonly nombreUsuario = signal<string>('Invitado');

  protected readonly inicialAvatar = computed<string>(() =>
    this.nombreUsuario()[0]?.toUpperCase() ?? 'I'
  );

  protected readonly saludo = computed<string>(() => {
    const hora = 12; // valor fijo para SSR/evitar globals
    if (hora < 12) return '¡Buenos días';
    if (hora < 19) return '¡Buenas tardes';
    return '¡Buenas noches';
  });

  // ── Señales de datos ────────────────────────────────────
  protected readonly playlists = signal<IPlaylist[]>([]);
  protected readonly artistas = signal<IUser[]>([]);
  protected readonly album = signal<IAlbum[]>([]);
  protected readonly canciones = signal<ICancion[]>([]);
  protected readonly proximosLanzamientos = signal<ILanzamiento[]>([]);
  protected readonly novedadesSemana = signal<ILanzamiento[]>([]);

  // ── Estados de carga ────────────────────────────────────
  protected readonly cargando = signal<boolean>(true);
  protected readonly error = signal<string | null>(null);

  ngOnInit(): void {
    const logueado = this.tokenService.isLogged();
    this.estaLogueado.set(logueado);
    this.cargarDatos(logueado);
  }

  private cargarDatos(logueado: boolean): void {
    this.cargando.set(true);
    this.error.set(null);

    if (logueado) {
      this.cargarDatosPersonalizados();
    } else {
      this.cargarDatosPublicos();
    }
  }

  private cargarDatosPublicos(): void {
    this.homeService.getPlaylistsDestacadas().subscribe({
      next: (data) => this.playlists.set(data),
      error: () => this.playlists.set([]),
    });

    this.homeService.getArtistasPopulares().subscribe({
      next: (data) => this.artistas.set(data),
      error: () => this.artistas.set([]),
    });

    this.homeService.getCancionesMasEscuchadas().subscribe({
      next: (data) => this.canciones.set(data),
      error: () => this.canciones.set([]),
    });

    this.homeService.getProximosLanzamientos().subscribe({
      next: (data) => this.proximosLanzamientos.set(data),
      error: () => this.proximosLanzamientos.set([]),
    });

    this.homeService.getNovedadesSemana().subscribe({
      next: (data) => {
        this.novedadesSemana.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.novedadesSemana.set([]);
        this.cargando.set(false);
      },
    });
  }

  private cargarDatosPersonalizados(): void {
    this.homeService.getMisPlaylists().subscribe({
      next: (data) => this.playlists.set(data),
      error: () => this.playlists.set([]),
    });

    this.homeService.getArtistasQueSigo().subscribe({
      next: (data) => this.artistas.set(data),
      error: () => this.artistas.set([]),
    });

    this.homeService.getMisCancionesMasEscuchadas().subscribe({
      next: (data) => this.canciones.set(data),
      error: () => this.canciones.set([]),
    });

    this.homeService.getMisProximosLanzamientos().subscribe({
      next: (data) => {
        this.proximosLanzamientos.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.proximosLanzamientos.set([]);
        this.cargando.set(false);
      },
    });

    this.homeService.getNovedadesSemana().subscribe({
      next: (data) => this.novedadesSemana.set(data),
      error: () => this.novedadesSemana.set([]),
    });
  }

  protected formatearDuracion(segundos: number): string {
    const m = Math.floor(segundos / 60);
    const s = segundos % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }
}
