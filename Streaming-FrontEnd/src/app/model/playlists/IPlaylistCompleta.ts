import {IPista} from '../pista/IPista';

export interface IPlaylistCompleta {
  id: number;
  nombre: string;
  descripcion: string;
  usernamePropietario: string;
  urlPortada: string;
  pistas: IPista[];
  esPropia: boolean;
}
