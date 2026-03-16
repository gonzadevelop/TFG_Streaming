import IPlaylist from './IPlaylist';
import IPista from './IPista';

export default interface IPlaylistPista {
  playlist: IPlaylist;
  pista: IPista;
  fechaAdicion?: Date;
}

