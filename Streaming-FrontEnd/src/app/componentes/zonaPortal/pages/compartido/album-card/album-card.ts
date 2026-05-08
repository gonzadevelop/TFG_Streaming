import { ChangeDetectionStrategy, Component, computed, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';

export interface AlbumCardData {
  id: number;
  titulo?: string;
  nombre?: string;
  artista?: string;
  urlPortada: string;
  descripcion?: string;
  tipo?: string;
  fechaLanzamiento?: string;
}

export type ContentType = 'playlist' | 'album' | 'sencillo';

@Component({
  selector: 'app-album-card',
  imports: [RouterLink],
  templateUrl: './album-card.html',
  styleUrl: './album-card.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlbumCard {
  readonly album = input.required<AlbumCardData>();
  readonly routerLink = input<string | string[] | null>(null);
  readonly showBadge = input<boolean>(false);
  readonly contentType = input<ContentType | null>(null);
  readonly playClick = output<AlbumCardData>();

  // Computed para obtener el título (compatible con 'titulo' o 'nombre')
  protected readonly titulo = computed(() => {
    const data = this.album();
    return data.titulo || data.nombre || '';
  });

  // Computed para obtener el label del tipo de contenido
  protected readonly contentTypeLabel = computed(() => {
    const type = this.contentType();
    if (!type) return null;

    const labels: Record<ContentType, string> = {
      playlist: '🎵 Playlist',
      album: '💿 Álbum',
      sencillo: '🎧 Sencillo',
    };

    return labels[type];
  });

  protected onPlayClick(event: Event): void {
    event.stopPropagation();
    this.playClick.emit(this.album());
  }
}


