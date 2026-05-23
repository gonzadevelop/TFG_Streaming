export interface ITopCancion {
  idPista: number;
  titulo: string;
  artistas: string[];
  artistasUsername?: string[];
  urlPortada: string;
  urlCancion: string;
  duracionSegundos: number;
  reproducciones: number;
}

export interface ITopArtista {
  username: string;
  nombre: string;
  urlFotoPerfil?: string;
  reproducciones: number;
}

export interface ITopAlbum {
  id: number;
  titulo: string;
  artista: string;
  artistas?: string[];
  urlPortada: string;
  reproducciones: number;
}

export interface IEstadistica {
  segundosEscuchadosMes: number;
  topCanciones: ITopCancion[];
  topArtistas: ITopArtista[];
  topAlbumes: ITopAlbum[];
}

