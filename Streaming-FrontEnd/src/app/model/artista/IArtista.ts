import {IAlbum} from '../album/IAlbum';
import {IPista} from '../pista/IPista';

export interface IArtista {
  username: string;
  urlAvatar: string;
  seguidores: number;
  cancionesEnFavoritos: number;
  canciones: IPista[];
  albums: IAlbum[];
}
