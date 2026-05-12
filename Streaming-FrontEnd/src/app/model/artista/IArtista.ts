import {IAlbum} from '../album/IAlbum';
import {IPista} from '../pista/IPista';

export interface IArtista {
  id: number;
  username: string;
  urlAvatar: string;
  seguidores: number;
  cancionesEnFavoritos: number;
  sigueAlArtista: boolean;
  canciones: IPista[];
  albums: IAlbum[];
}
