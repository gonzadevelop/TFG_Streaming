import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  inject,
  signal,
  WritableSignal,
} from '@angular/core';
import { Router } from '@angular/router';
import { IPlaylistCompleta } from '../../../../../model/playlists/IPlaylistCompleta';
import { ListaCanciones } from '../compartido/lista-canciones/lista-canciones';
import { PlaylistService } from '../../../../../services/playlistService';
import { StorageGlobal } from '../../../../../services/storageGlobal';
import IPistaCola from '../../../../../model/pista/IPistaCola';

@Component({
  selector: 'app-playlist',
  imports: [ListaCanciones],
  templateUrl: './playlist.html',
  styleUrl: './playlist.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Playlist implements OnInit {
  private readonly playlistService: PlaylistService = inject(PlaylistService);
  private readonly router: Router = inject(Router);
  private readonly storage = inject(StorageGlobal);

  /**
   * Playlist obtenida del backend
   */
  readonly playlist: WritableSignal<IPlaylistCompleta | null> = signal<IPlaylistCompleta | null>(null);

  protected readonly cargando: WritableSignal<boolean> = signal(true);
  protected readonly error: WritableSignal<string | null> = signal<string | null>(null);

  ngOnInit(): void {
    this.cargando.set(true);
    this.error.set(null);

    // Obtener la ruta entera del navegador
    const rutaCompleta: string = this.router.url;

    this.playlistService.getPlaylist(rutaCompleta).subscribe({
      next: (data: IPlaylistCompleta) => {
        this.playlist.set(data);
        this.cargando.set(false);
        console.log('Playlist cargada:', this.playlist());
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
}
