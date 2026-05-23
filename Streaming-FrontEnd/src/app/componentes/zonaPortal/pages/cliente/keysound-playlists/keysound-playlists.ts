import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  inject,
  signal,
  WritableSignal,
} from '@angular/core';
import { RouterLink } from '@angular/router';
import { PlaylistService } from '../../../../../services/playlistService';
import { IPlaylist } from '../../../../../model/home/IPlaylist';
import { KsLoaderComponent } from '../compartido/ks-loader/ks-loader';

@Component({
  selector: 'app-keysound-playlists',
  imports: [RouterLink, KsLoaderComponent],
  templateUrl: './keysound-playlists.html',
  styleUrl: './keysound-playlists.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Playlists implements OnInit {

  private readonly playlistService: PlaylistService = inject(PlaylistService);

  // ── Señales de datos ────────────────────────────────────
  protected readonly playlists: WritableSignal<IPlaylist[]> = signal<IPlaylist[]>([]);

  // ── Estados de carga ────────────────────────────────────
  protected readonly cargando = signal<boolean>(true);
  protected readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.cargarPlaylists();
  }

  private cargarPlaylists(): void {
    this.playlistService.getPlaylistsKeysound().subscribe({
      next: (data) => {
        this.playlists.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error al cargar playlists de KeySound:', err);
        this.error.set('No se pudieron cargar las playlists. Intenta nuevamente más tarde.');
        this.cargando.set(false);
      }
    });
  }
}
