import ICancion from './ICancion';
import {IAlbum} from './IAlbum';

export default interface IPlaylistKeysound {
  id: number;
  numeroPista: number;
  cancionId: ICancion;
  albumId: IAlbum;
}
