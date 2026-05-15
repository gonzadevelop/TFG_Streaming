import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ArtistaService } from '../../../../../services/artistaService';
import { IMiAlbum } from '../../../../../model/album/IMiAlbum';

@Component({
  selector: 'app-artista-albums',
  imports: [RouterLink],
  templateUrl: './artista-albums.html',
  styleUrl: './artista-albums.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ArtistaAlbums implements OnInit {
  private readonly artistaService = inject(ArtistaService);

  protected readonly albums = signal<IMiAlbum[]>([]);
  protected readonly isLoading = signal<boolean>(true);
  protected readonly errorMessage = signal<string>('');
  protected readonly processingIds = signal<Set<number>>(new Set());

  ngOnInit(): void {
    this.cargarAlbums();
  }

  private cargarAlbums(): void {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.artistaService.getMisAlbums().subscribe({
      next: (albums) => {
        this.albums.set(albums ?? []);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('No se pudieron cargar tus álbumes.');
        this.isLoading.set(false);
      },
    });
  }

  protected publicar(album: IMiAlbum): void {
    if (this.processingIds().has(album.id)) return;

    const updated = new Set(this.processingIds());
    updated.add(album.id);
    this.processingIds.set(updated);

    this.artistaService.publicarAlbum(album.id).subscribe({
      next: () => {
        this.albums.update(items =>
          items.map(item =>
            item.id === album.id ? { ...item, esBorrador: false } : item
          )
        );
        this.finalizarProceso(album.id);
      },
      error: () => {
        this.errorMessage.set('No se pudo publicar el álbum.');
        this.finalizarProceso(album.id);
      },
    });
  }

  protected eliminar(album: IMiAlbum): void {
    if (this.processingIds().has(album.id)) return;
    if (!confirm(`¿Seguro que quieres eliminar "${album.titulo}"?`)) return;

    const updated = new Set(this.processingIds());
    updated.add(album.id);
    this.processingIds.set(updated);

    this.artistaService.eliminarAlbum(album.id).subscribe({
      next: () => {
        this.albums.update(items => items.filter(item => item.id !== album.id));
        this.finalizarProceso(album.id);
      },
      error: () => {
        this.errorMessage.set('No se pudo eliminar el álbum.');
        this.finalizarProceso(album.id);
      },
    });
  }

  private finalizarProceso(id: number): void {
    const updated = new Set(this.processingIds());
    updated.delete(id);
    this.processingIds.set(updated);
  }
}
