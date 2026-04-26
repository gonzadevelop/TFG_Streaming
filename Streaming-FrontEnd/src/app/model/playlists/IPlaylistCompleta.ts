import {IPistaPlaylist} from '../pista/IPistaPlaylist';

export interface IPlaylistCompleta {
  id: number;
  nombre: string;
  descripcion: string;
  usernamePropietario: string;
  urlPortada: string;
  pistas: IPistaPlaylist[];
  esPropia: boolean;
}
