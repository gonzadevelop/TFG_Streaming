import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  inject,
  signal,
  WritableSignal,
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { KeySoundPlaylistsService } from '../../../../services/keySoundPlaylistsService';
import { IPlaylistCompleta } from '../../../../model/playlists/IPlaylistCompleta';
import {Playlist} from '../compartido/playlist/playlist';

@Component({
  selector: 'app-top30-diario',
  imports: [
    Playlist
  ],
  templateUrl: './top30-diario.html',
  styleUrl: './top30-diario.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Top30Diario implements OnInit {
  private readonly service = inject(KeySoundPlaylistsService);
  private readonly route = inject(ActivatedRoute);

  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);

  protected readonly playlist: WritableSignal<IPlaylistCompleta | null> = signal<IPlaylistCompleta | null>(null);

  ngOnInit(): void {
    const nombre = this.route.snapshot.paramMap.get('nombre') ?? '';
    const fecha = this.route.snapshot.queryParamMap.get('fecha') ?? undefined;

    this.cargando.set(true);
    this.error.set(null);

    this.service.getPlaylistByNombre(nombre, fecha).subscribe({
      next: (data: IPlaylistCompleta) => {
        this.playlist.set(data);
        this.cargando.set(false);
        console.log(this.playlist);
      },
      error: (err: unknown) => {
        console.error('Error cargando playlist:', err);
        this.error.set('No se pudo cargar la playlist.');
        this.cargando.set(false);
      },
    });
  }
}

