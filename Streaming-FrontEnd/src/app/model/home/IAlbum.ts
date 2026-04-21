import {IMiniArtista} from './IMiniArtista';

export interface IAlbum {
  id: number;
  titulo: string;
  artista: IMiniArtista;
  urlPortada: string;
  anioLanzamiento: number;
  tipo: 'álbum' | 'sencillo';
}
