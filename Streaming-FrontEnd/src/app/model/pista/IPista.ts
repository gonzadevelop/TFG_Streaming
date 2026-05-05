export interface IPista {
  idPista: number;
  titulo: string;
  artistas: string[];
  urlPortada: string;
  urlCancion: string;
  reproducciones?: number;
  duracionSegundos: number;
  numeroPista?: number;
  albumId?: number;

  // Campos del DTO de búsqueda

  id?: number;
  artista?: string;
  caratula?: string;
  album?: string;
}
