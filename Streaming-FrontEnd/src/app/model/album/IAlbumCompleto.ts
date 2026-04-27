import {IPista} from '../pista/IPista';

export interface IAlbumCompleto {
  titulo: string;
  portada: string;
  artista: string;
  anioLanzamiento: number;
  duracionTotalSegundos: number;
  numCanciones: number;
  tipo: 'álbum' | 'sencillo';
  canciones: IPista[];
}
