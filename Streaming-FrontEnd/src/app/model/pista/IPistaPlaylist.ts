import {IMiniArtista} from '../home/IMiniArtista';

export interface IPistaPlaylist {
  idCancion: number;
  titulo: string;
  artistas: IMiniArtista[];
  urlPortada: string;
  urlCancion: string;
  reproducciones?: number;
  duracionSegundos: number;
  numeroPista?: number;
}
