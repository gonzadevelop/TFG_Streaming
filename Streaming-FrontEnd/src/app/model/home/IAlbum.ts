export interface IAlbum {
  id: number;
  titulo: string;
  artista: string;
  urlPortada: string;
  anioLanzamiento: number;
  tipo: 'álbum' | 'sencillo';
}
