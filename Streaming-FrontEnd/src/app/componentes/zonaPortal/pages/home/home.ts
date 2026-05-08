import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  computed,
  inject,
  signal, WritableSignal,
} from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { TokenService } from '../../../../services/tokenService';
import { HomeService } from '../../../../services/homeService';
import { PlaylistService } from '../../../../services/playlistService';
import { IHome } from '../../../../model/home/IHome';
import { IPlaylist } from '../../../../model/home/IPlaylist';
import { ListaCanciones } from '../compartido/lista-canciones/lista-canciones';
import { AlbumCard } from '../compartido/album-card/album-card';

@Component({
  selector: 'app-home',
  imports: [RouterLink, ListaCanciones, ReactiveFormsModule, AlbumCard],
  templateUrl: './home.html',
  styleUrl: './home.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Home implements OnInit {

  private readonly tokenService = inject(TokenService);
  private readonly homeService  = inject(HomeService);
  private readonly playlistService = inject(PlaylistService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

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
  homeData: WritableSignal<IHome> = signal<IHome>({
    keySoundPlaylists: [],
    artistasSeguidos: [],
    misPlaylist: [],
    novedadesDeLaSemana: [],
    proximosLanzmientos: [],
    cancionesMasEscuchadas: [],
  });

  // ── Estados de carga ────────────────────────────────────
  protected readonly cargando = signal<boolean>(true);
  protected readonly error = signal<string | null>(null);

  // ── Modal creación playlist ──────────────────────────────
  protected readonly modalAbierto   = signal(false);
  protected readonly creando        = signal(false);
  protected readonly errorModal     = signal<string | null>(null);
  protected readonly portadaPreview = signal<string | null>(null);
  protected readonly portadaFile    = signal<File | null>(null);
  protected readonly draggingOver   = signal(false);

  protected readonly playlistForm: FormGroup = this.fb.group({
    nombre:      ['', [Validators.required, Validators.maxLength(100)]],
    descripcion: ['', Validators.maxLength(300)],
    esPublica:   [true],
  });

  ngOnInit(): void {
    this.estaLogueado.set(this.tokenService.isLogged());
    this.cargarDatos();
  }

  private cargarDatos(): void {
    forkJoin({
      home:         this.homeService.getDatosHome().pipe(catchError(() => of(null))),
      misPlaylists: this.playlistService.getMisPlaylists().pipe(catchError(() => of([] as IPlaylist[]))),
    }).subscribe({
      next: ({ home, misPlaylists }) => {
        this.homeData.set({
          keySoundPlaylists:      home?.keySoundPlaylists      ?? [],
          artistasSeguidos:       home?.artistasSeguidos       ?? [],
          misPlaylist:            misPlaylists                 ?? [],
          novedadesDeLaSemana:    home?.novedadesDeLaSemana    ?? [],
          proximosLanzmientos:    home?.proximosLanzmientos    ?? [],
          cancionesMasEscuchadas: home?.cancionesMasEscuchadas ?? [],
        });
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar los datos. Intenta nuevamente más tarde.');
        this.cargando.set(false);
      },
    });
  }

  protected navegarArtista(username: string): void {
    this.router.navigate(['/artistas', username]);
  }

  // ── Modal ────────────────────────────────────────────────
  protected abrirModal(): void {
    this.playlistForm.reset({ nombre: '', descripcion: '', esPublica: true });
    this.portadaPreview.set(null);
    this.portadaFile.set(null);
    this.errorModal.set(null);
    this.modalAbierto.set(true);
  }

  protected cerrarModal(): void {
    if (this.creando()) return;
    this.modalAbierto.set(false);
  }

  protected onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.draggingOver.set(true);
  }

  protected onDragLeave(): void {
    this.draggingOver.set(false);
  }

  protected onDrop(event: DragEvent): void {
    event.preventDefault();
    this.draggingOver.set(false);
    const file = event.dataTransfer?.files[0];
    if (file) this.procesarImagen(file);
  }

  protected onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (file) this.procesarImagen(file);
  }

  private procesarImagen(file: File): void {
    if (!file.type.startsWith('image/')) {
      this.errorModal.set('El archivo debe ser una imagen.');
      return;
    }
    this.portadaFile.set(file);
    const reader = new FileReader();
    reader.onload = (e) => this.portadaPreview.set(e.target?.result as string);
    reader.readAsDataURL(file);
  }

  protected quitarPortada(): void {
    this.portadaPreview.set(null);
    this.portadaFile.set(null);
  }

  protected crearPlaylist(): void {
    if (this.playlistForm.invalid || this.creando()) return;
    this.creando.set(true);
    this.errorModal.set(null);

    const { nombre, descripcion, esPublica } = this.playlistForm.value as {
      nombre: string; descripcion: string; esPublica: boolean;
    };

    this.playlistService.setCrearPlaylist({
      nombre, descripcion, esPublica,
      fotoPortada: this.portadaFile() ?? undefined,
    }).subscribe({
      next: () => {
        this.creando.set(false);
        this.modalAbierto.set(false);
        // Recargar mis playlists tras crear
        this.playlistService.getMisPlaylists().pipe(catchError(() => of([] as IPlaylist[]))).subscribe({
          next: (playlists) => this.homeData.update(d => ({ ...d, misPlaylist: playlists ?? [] })),
        });
      },
      error: () => {
        this.creando.set(false);
        this.errorModal.set('No se pudo crear la playlist. Inténtalo de nuevo.');
      },
    });
  }
}
