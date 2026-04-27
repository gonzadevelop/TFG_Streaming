export interface IAlbum {
  id: number;
  artista: string;
  titulo: string;
  urlPortada: string;
  anioLanzamiento: number;
  tipo: 'álbum' | 'sencillo';
}
