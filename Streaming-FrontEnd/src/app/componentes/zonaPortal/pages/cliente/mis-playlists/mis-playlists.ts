import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnInit,
  signal,
  WritableSignal,
} from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { catchError, of } from 'rxjs';
import { PlaylistService } from '../../../../../services/playlistService';
import { IPlaylist } from '../../../../../model/home/IPlaylist';
import { IPlaylistRequest } from '../../../../../model/playlists/IPlaylistRequest';
import { AlbumCard } from '../compartido/album-card/album-card';
import { KsLoaderComponent } from '../compartido/ks-loader/ks-loader';
import { ScrollRevealDirective } from '../../../../../shared/directives/scroll-reveal.directive';

@Component({
  selector: 'app-mis-playlists',
  imports: [RouterLink, ReactiveFormsModule, AlbumCard, KsLoaderComponent, ScrollRevealDirective],
  templateUrl: './mis-playlists.html',
  styleUrl: './mis-playlists.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MisPlaylists implements OnInit {
  private readonly playlistService = inject(PlaylistService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  protected readonly playlists: WritableSignal<IPlaylist[]> = signal<IPlaylist[]>([]);
  protected readonly cargando = signal<boolean>(true);
  protected readonly error = signal<string | null>(null);

  // ── Modal crear ──────────────────────────────────────────
  protected readonly modalAbierto   = signal(false);
  protected readonly creando        = signal(false);
  protected readonly errorModal     = signal<string | null>(null);
  protected readonly portadaPreview = signal<string | null>(null);
  protected readonly portadaFile    = signal<File | null>(null);
  protected readonly draggingOver   = signal(false);

  // ── Modal editar ─────────────────────────────────────────
  protected readonly editModalAbierto   = signal(false);
  protected readonly editando           = signal(false);
  protected readonly errorEditModal     = signal<string | null>(null);
  protected readonly editPortadaPreview = signal<string | null>(null);
  protected readonly editPortadaFile    = signal<File | null>(null);
  protected readonly editDraggingOver   = signal(false);
  protected readonly editPlaylistId     = signal<number | null>(null);

  // ── Confirmación borrar ──────────────────────────────────
  protected readonly confirmDeleteId = signal<number | null>(null);
  protected readonly eliminando      = signal(false);

  protected readonly playlistForm: FormGroup = this.fb.group({
    nombre:      ['', [Validators.required, Validators.maxLength(100)]],
    descripcion: ['', Validators.maxLength(300)],
    esPublica:   [true],
  });

  protected readonly editPlaylistForm: FormGroup = this.fb.group({
    nombre:      ['', [Validators.required, Validators.maxLength(100)]],
    descripcion: ['', Validators.maxLength(300)],
    esPublica:   [true],
  });

  ngOnInit(): void {
    this.cargarPlaylists();
  }

  private cargarPlaylists(): void {
    this.cargando.set(true);
    this.playlistService.getMisPlaylists().pipe(catchError(() => of([] as IPlaylist[]))).subscribe({
      next: (data) => {
        this.playlists.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar tus playlists.');
        this.cargando.set(false);
      },
    });
  }

  // ── Modal crear ──────────────────────────────────────────
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

  protected crearPlaylist(): void {
    if (this.playlistForm.invalid) return;
    this.creando.set(true);
    this.errorModal.set(null);

    const { nombre, descripcion, esPublica } = this.playlistForm.value;
    const dto: IPlaylistRequest = { nombre, descripcion, esPublica, fotoPortada: this.portadaFile() ?? undefined };

    this.playlistService.setCrearPlaylist(dto).subscribe({
      next: () => {
        this.creando.set(false);
        this.modalAbierto.set(false);
        this.cargarPlaylists();
      },
      error: () => {
        this.errorModal.set('Error al crear la playlist. Inténtalo de nuevo.');
        this.creando.set(false);
      },
    });
  }

  protected onDragOver(e: DragEvent): void { e.preventDefault(); this.draggingOver.set(true); }
  protected onDragLeave(): void { this.draggingOver.set(false); }
  protected onDrop(e: DragEvent): void {
    e.preventDefault(); this.draggingOver.set(false);
    const file = e.dataTransfer?.files?.[0];
    if (file) this.procesarPortada(file);
  }
  protected onFileSelected(e: Event): void {
    const file = (e.target as HTMLInputElement).files?.[0];
    if (file) this.procesarPortada(file);
  }
  protected quitarPortada(): void { this.portadaPreview.set(null); this.portadaFile.set(null); }

  private procesarPortada(file: File): void {
    const reader = new FileReader();
    reader.onload = (e) => this.portadaPreview.set(e.target?.result as string);
    reader.readAsDataURL(file);
    this.portadaFile.set(file);
  }

  // ── Modal editar ─────────────────────────────────────────
  protected abrirEditModal(playlist: IPlaylist, event: Event): void {
    event.stopPropagation();
    this.editPlaylistId.set(playlist.id);
    this.editPlaylistForm.reset({
      nombre: playlist.nombre,
      descripcion: playlist.descripcion ?? '',
      esPublica: playlist.esPublica ?? true,
    });
    this.editPortadaPreview.set(playlist.urlPortada ?? null);
    this.editPortadaFile.set(null);
    this.errorEditModal.set(null);
    this.editModalAbierto.set(true);
  }

  protected cerrarEditModal(): void {
    if (this.editando()) return;
    this.editModalAbierto.set(false);
  }

  protected editarPlaylist(): void {
    if (this.editPlaylistForm.invalid) return;
    const id = this.editPlaylistId();
    if (!id) return;

    this.editando.set(true);
    this.errorEditModal.set(null);

    const { nombre, descripcion, esPublica } = this.editPlaylistForm.value;
    const dto: IPlaylistRequest = { nombre, descripcion, esPublica, fotoPortada: this.editPortadaFile() ?? undefined };

    this.playlistService.setEditarPlaylist(id, dto).subscribe({
      next: () => {
        this.editando.set(false);
        this.editModalAbierto.set(false);
        this.cargarPlaylists();
      },
      error: () => {
        this.errorEditModal.set('Error al guardar los cambios. Inténtalo de nuevo.');
        this.editando.set(false);
      },
    });
  }

  protected onEditDragOver(e: DragEvent): void { e.preventDefault(); this.editDraggingOver.set(true); }
  protected onEditDragLeave(): void { this.editDraggingOver.set(false); }
  protected onEditDrop(e: DragEvent): void {
    e.preventDefault(); this.editDraggingOver.set(false);
    const file = e.dataTransfer?.files?.[0];
    if (file) this.procesarPortadaEdit(file);
  }
  protected onEditFileSelected(e: Event): void {
    const file = (e.target as HTMLInputElement).files?.[0];
    if (file) this.procesarPortadaEdit(file);
  }
  protected quitarPortadaEdit(): void { this.editPortadaPreview.set(null); this.editPortadaFile.set(null); }

  private procesarPortadaEdit(file: File): void {
    const reader = new FileReader();
    reader.onload = (e) => this.editPortadaPreview.set(e.target?.result as string);
    reader.readAsDataURL(file);
    this.editPortadaFile.set(file);
  }

  // ── Eliminar ─────────────────────────────────────────────
  protected pedirConfirmacionEliminar(id: number, event: Event): void {
    event.stopPropagation();
    this.confirmDeleteId.set(id);
  }

  protected cancelarEliminar(): void {
    this.confirmDeleteId.set(null);
  }

  protected eliminarPlaylist(): void {
    const id = this.confirmDeleteId();
    if (!id) return;
    this.eliminando.set(true);
    this.playlistService.eliminarPlaylist(id).subscribe({
      next: () => {
        this.eliminando.set(false);
        this.confirmDeleteId.set(null);
        this.cargarPlaylists();
      },
      error: () => {
        this.eliminando.set(false);
        this.confirmDeleteId.set(null);
      },
    });
  }

  protected volver(): void {
    this.router.navigate(['/']);
  }
}



