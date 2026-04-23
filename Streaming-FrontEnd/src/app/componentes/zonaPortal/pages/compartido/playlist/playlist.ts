import {
  ChangeDetectionStrategy,
  Component,
  input,
} from '@angular/core';
import { IPlaylistCompleta } from '../../../../../model/playlists/IPlaylistCompleta';
import { ListaCanciones } from '../lista-canciones/lista-canciones';

@Component({
  selector: 'app-playlist',
  imports: [ListaCanciones],
  templateUrl: './playlist.html',
  styleUrl: './playlist.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Playlist {
  /**
   * Playlist ya preparada (por ejemplo, vía resolver en la ruta o desde un componente padre).
   */
  readonly playlist = input.required<IPlaylistCompleta>();
}
