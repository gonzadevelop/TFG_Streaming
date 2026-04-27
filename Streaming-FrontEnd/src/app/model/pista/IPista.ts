export interface IPista {
  idPista: number;
  titulo: string;
  artistas: string[];
  urlPortada: string;
  urlCancion: string;
  reproducciones?: number;
  duracionSegundos: number;
  numeroPista?: number;
}
