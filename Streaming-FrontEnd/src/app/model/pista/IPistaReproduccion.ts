import {IMiniArtista} from '../home/IMiniArtista';

export default interface IPistaReproduccion {
  idPista: number;
  titulo: string;
  artistas: IMiniArtista[];
  urlPortada: string;
  urlCancion: string;
  duracionSegundos: number;
  reproduciendo: boolean;
}
