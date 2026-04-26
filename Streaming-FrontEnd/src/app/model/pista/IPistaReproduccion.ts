export default interface IPistaReproduccion {
  idPista: number;
  titulo: string;
  artistas: string[];
  urlPortada: string;
  urlCancion: string;
  duracionSegundos: number;
  reproduciendo: boolean;
}
