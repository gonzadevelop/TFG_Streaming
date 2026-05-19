import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  inject,
  signal,
  computed,
  WritableSignal,
} from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { IPlaylistCompleta } from '../../../../../model/playlists/IPlaylistCompleta';
import { ListaCanciones } from '../compartido/lista-canciones/lista-canciones';
import { PlaylistService } from '../../../../../services/playlistService';
import { StorageGlobal } from '../../../../../services/storageGlobal';
import IPistaCola from '../../../../../model/pista/IPistaCola';
import { TokenService } from '../../../../../services/tokenService';

@Component({
  selector: 'app-playlist',
  imports: [ListaCanciones, ReactiveFormsModule],
  templateUrl: './playlist.html',
  styleUrl: './playlist.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Playlist implements OnInit {
  private readonly playlistService = inject(PlaylistService);
  private readonly router = inject(Router);
  private readonly storage = inject(StorageGlobal);
  private readonly fb = inject(FormBuilder);
  private readonly tokenService = inject(TokenService);

  readonly playlist: WritableSignal<IPlaylistCompleta | null> = signal<IPlaylistCompleta | null>(null);

  protected readonly cargando: WritableSignal<boolean> = signal(true);
  protected readonly error: WritableSignal<string | null> = signal<string | null>(null);

  /** Verdadero si la playlist pertenece al usuario autenticado */
  protected readonly esPropia = computed(() => {
    const p = this.playlist();
    if (!p) return false;
    if (p.esPropia) return true;
    // Extraer el username directamente del JWT (más fiable que getUsername, que puede estar vacío)
    const username = this.tokenService.getUsernameFromToken() ?? this.tokenService.getUsername();
    if (!username || username === 'Invitado') return false;
    return p.usernamePropietario?.toLowerCase() === username.toLowerCase();
  });

  // ── Modal edición ────────────────────────────────────────
  protected readonly editModalAbierto = signal(false);
  protected readonly editando         = signal(false);
  protected readonly errorEdit        = signal<string | null>(null);
  protected readonly portadaPreview   = signal<string | null>(null);
  protected readonly portadaFile      = signal<File | null>(null);
  protected readonly draggingOver     = signal(false);

  protected readonly editForm: FormGroup = this.fb.group({
    nombre:      ['', [Validators.required, Validators.maxLength(100)]],
    descripcion: ['', Validators.maxLength(300)],
    esPublica:   [true],
  });

  ngOnInit(): void {
    this.cargando.set(true);
    this.error.set(null);

    // Obtener la ruta entera del navegador
    const rutaCompleta: string = this.router.url;

    this.playlistService.getPlaylist(rutaCompleta).subscribe({
      next: (data: IPlaylistCompleta) => {
        this.playlist.set(data);
        this.cargando.set(false);
        console.log('Playlist cargada:', data);
        console.log('usernamePropietario recibido:', data.usernamePropietario);
      },
      error: (err: unknown) => {
        console.error('Error cargando playlist:', err);
        this.error.set('No se pudo cargar la playlist.');
        this.cargando.set(false);
      },
    });
  }

  protected reproducirTodo(): void {
    const playlistData = this.playlist();
    if (!playlistData || !playlistData.pistas || playlistData.pistas.length === 0) return;

    const cola: IPistaCola[] = playlistData.pistas.map((p, index) => ({
      ...p,
      orden: index, // Add orden here properly matching definition
      reproduciendo: index === 0
    }));
    this.storage.SetCola(cola);
  }

  // ── Modal ────────────────────────────────────────────────
  protected abrirEditModal(): void {
    const p = this.playlist();
    if (!p) return;
    this.editForm.reset({ nombre: p.nombre, descripcion: p.descripcion ?? '', esPublica: true });
    this.portadaPreview.set(p.urlPortada ?? null);
    this.portadaFile.set(null);
    this.errorEdit.set(null);
    this.editModalAbierto.set(true);
  }

  protected cerrarEditModal(): void {
    if (this.editando()) return;
    this.editModalAbierto.set(false);
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
      this.errorEdit.set('El archivo debe ser una imagen.');
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

  protected onCancionEliminada(idPista: number): void {
    this.playlist.update(p => {
      if (!p) return p;
      return { ...p, pistas: p.pistas.filter(pista => pista.idPista !== idPista && pista.id !== idPista) };
    });
  }

  protected guardarEdicion(): void {
    const p = this.playlist();
    if (this.editForm.invalid || this.editando() || !p) return;
    this.editando.set(true);
    this.errorEdit.set(null);

    const { nombre, descripcion, esPublica } = this.editForm.value as {
      nombre: string; descripcion: string; esPublica: boolean;
    };

    this.playlistService.setEditarPlaylist(p.id, {
      nombre, descripcion, esPublica,
      fotoPortada: this.portadaFile() ?? undefined,
    }).subscribe({
      next: () => {
        this.editando.set(false);
        this.editModalAbierto.set(false);
        // Refrescar datos de la playlist
        this.playlistService.getPlaylist(this.router.url).subscribe({
          next: (data) => this.playlist.set(data),
        });
      },
      error: () => {
        this.editando.set(false);
        this.errorEdit.set('No se pudo guardar los cambios. Inténtalo de nuevo.');
      },
    });
  }
}
