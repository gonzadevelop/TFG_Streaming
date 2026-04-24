import { IMiniArtista } from './IMiniArtista';

export interface IPistaHome {
  cancionId: number;
  albumId: number;
  titulo: string;
  urlPortada: string;
  artistas: IMiniArtista[];
  urlCancion: string;
  duracionSegundos: number;
  reproduccionesDelUsuario: number;
}
