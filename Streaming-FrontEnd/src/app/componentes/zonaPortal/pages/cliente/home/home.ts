import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal, WritableSignal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { catchError, forkJoin, of } from 'rxjs';
import { TokenService } from '../../../../../services/tokenService';
import { HomeService } from '../../../../../services/homeService';
import { PlaylistService } from '../../../../../services/playlistService';
import { AlbumService } from '../../../../../services/albumService';
import { IHome } from '../../../../../model/home/IHome';
import { IPlaylist } from '../../../../../model/home/IPlaylist';
import { IAlbum } from '../../../../../model/album/IAlbum';
import { ListaCanciones } from '../compartido/lista-canciones/lista-canciones';
import { AlbumCard } from '../compartido/album-card/album-card';
import { KsLoaderComponent } from '../compartido/ks-loader/ks-loader';
import { ScrollRevealDirective } from '../../../../../shared/directives/scroll-reveal.directive';

@Component({
  selector: 'app-home',
  imports: [RouterLink, ListaCanciones, ReactiveFormsModule, AlbumCard, KsLoaderComponent, ScrollRevealDirective],
  templateUrl: './home.html',
  styleUrl: './home.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Home implements OnInit {

  private readonly tokenService = inject(TokenService);
  private readonly homeService  = inject(HomeService);
  private readonly playlistService = inject(PlaylistService);
  private readonly albumService = inject(AlbumService);
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
      home:                this.homeService.getDatosHome().pipe(catchError(() => of(null))),
      keysoundPlaylists:   this.playlistService.getPlaylistsKeysound().pipe(catchError(() => of([] as IPlaylist[]))),
      misPlaylists:        this.playlistService.getMisPlaylists().pipe(catchError(() => of([] as IPlaylist[]))),
      novedades:           this.albumService.getNovedades().pipe(catchError(() => of([] as IAlbum[]))),
      proximosLanzamientos: this.albumService.getProximosLanzamientos().pipe(catchError(() => of([] as IAlbum[]))),
    }).subscribe({
      next: ({ home, keysoundPlaylists, misPlaylists, novedades, proximosLanzamientos }: {
        home: IHome | null;
        keysoundPlaylists: IPlaylist[];
        misPlaylists: IPlaylist[];
        novedades: IAlbum[];
        proximosLanzamientos: IAlbum[];
      }) => {
        this.homeData.set({
          keySoundPlaylists:      keysoundPlaylists            ?? [],
          artistasSeguidos:       home?.artistasSeguidos       ?? [],
          misPlaylist:            misPlaylists                 ?? [],
          novedadesDeLaSemana:    novedades                   ?? [],
          proximosLanzmientos:    proximosLanzamientos        ?? [],
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

  // ── Modal edición playlist ───────────────────────────────
  protected readonly editModalAbierto     = signal(false);
  protected readonly editando             = signal(false);
  protected readonly errorEditModal       = signal<string | null>(null);
  protected readonly editPortadaPreview   = signal<string | null>(null);
  protected readonly editPortadaFile      = signal<File | null>(null);
  protected readonly editDraggingOver     = signal(false);
  protected readonly editPlaylistId       = signal<number | null>(null);
  protected readonly confirmDeleteId      = signal<number | null>(null);
  protected readonly eliminando           = signal(false);

  protected readonly editPlaylistForm: FormGroup = this.fb.group({
    nombre:      ['', [Validators.required, Validators.maxLength(100)]],
    descripcion: ['', Validators.maxLength(300)],
    esPublica:   [true],
  });

  protected abrirEditModal(playlist: IPlaylist, event: MouseEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.editPlaylistId.set(playlist.id);
    this.editPlaylistForm.reset({ nombre: playlist.nombre, descripcion: playlist.descripcion, esPublica: true });
    this.editPortadaPreview.set(playlist.urlPortada ?? null);
    this.editPortadaFile.set(null);
    this.errorEditModal.set(null);
    this.editModalAbierto.set(true);
  }

  protected cerrarEditModal(): void {
    if (this.editando()) return;
    this.editModalAbierto.set(false);
  }

  protected onEditDragOver(event: DragEvent): void {
    event.preventDefault();
    this.editDraggingOver.set(true);
  }

  protected onEditDragLeave(): void {
    this.editDraggingOver.set(false);
  }

  protected onEditDrop(event: DragEvent): void {
    event.preventDefault();
    this.editDraggingOver.set(false);
    const file = event.dataTransfer?.files[0];
    if (file) this.procesarImagenEdit(file);
  }

  protected onEditFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (file) this.procesarImagenEdit(file);
  }

  private procesarImagenEdit(file: File): void {
    if (!file.type.startsWith('image/')) {
      this.errorEditModal.set('El archivo debe ser una imagen.');
      return;
    }
    this.editPortadaFile.set(file);
    const reader = new FileReader();
    reader.onload = (e) => this.editPortadaPreview.set(e.target?.result as string);
    reader.readAsDataURL(file);
  }

  protected quitarPortadaEdit(): void {
    this.editPortadaPreview.set(null);
    this.editPortadaFile.set(null);
  }

  protected editarPlaylist(): void {
    const id = this.editPlaylistId();
    if (this.editPlaylistForm.invalid || this.editando() || id === null) return;
    this.editando.set(true);
    this.errorEditModal.set(null);

    const { nombre, descripcion, esPublica } = this.editPlaylistForm.value as {
      nombre: string; descripcion: string; esPublica: boolean;
    };

    this.playlistService.setEditarPlaylist(id, {
      nombre, descripcion, esPublica,
      fotoPortada: this.editPortadaFile() ?? undefined,
    }).subscribe({
      next: () => {
        this.editando.set(false);
        this.editModalAbierto.set(false);
        this.playlistService.getMisPlaylists().pipe(catchError(() => of([] as IPlaylist[]))).subscribe({
          next: (playlists) => this.homeData.update((d: IHome) => ({ ...d, misPlaylist: playlists ?? [] })),
        });
      },
      error: () => {
        this.editando.set(false);
        this.errorEditModal.set('No se pudo editar la playlist. Inténtalo de nuevo.');
      },
    });
  }

  protected pedirConfirmacionEliminar(id: number, event: MouseEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.confirmDeleteId.set(id);
  }

  protected cancelarEliminar(): void {
    this.confirmDeleteId.set(null);
  }

  protected eliminarPlaylist(): void {
    const id = this.confirmDeleteId();
    if (id === null || this.eliminando()) return;
    this.eliminando.set(true);
    this.playlistService.eliminarPlaylist(id).subscribe({
      next: () => {
        this.eliminando.set(false);
        this.confirmDeleteId.set(null);
        this.homeData.update((d: IHome) => ({ ...d, misPlaylist: d.misPlaylist.filter(p => p.id !== id) }));
      },
      error: () => {
        this.eliminando.set(false);
        this.errorEditModal.set('No se pudo eliminar la playlist.');
      },
    });
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
          next: (playlists) => this.homeData.update((d: IHome) => ({ ...d, misPlaylist: playlists ?? [] })),
        });
      },
      error: () => {
        this.creando.set(false);
        this.errorModal.set('No se pudo crear la playlist. Inténtalo de nuevo.');
      },
    });
  }
}
