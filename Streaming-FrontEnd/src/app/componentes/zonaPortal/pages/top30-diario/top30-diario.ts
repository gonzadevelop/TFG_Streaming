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
    // opcional: permitir /top30-diario?fecha=YYYY-MM-DD
    const fecha = this.route.snapshot.queryParamMap.get('fecha') ?? undefined;

    this.cargando.set(true);
    this.error.set(null);

    this.service.getDailyTop30(fecha).subscribe({
      next: (data) => {
        this.playlist.set(data);
        this.cargando.set(false);
        console.log(this.playlist);
      },
      error: (err) => {
        console.error('Error cargando dailyTop30:', err);
        this.error.set('No se pudo cargar el Top 30 diario.');
        this.cargando.set(false);
      },
    });
  }
}

