export interface IProximoAlbum {
  id: number;
  titulo: string;
  artista: string;
  urlPortada: string;
  fechaLanzamiento: string; // ISO 8601 (e.g., "2026-05-15T10:00:00")
  tipo: 'álbum' | 'sencillo';
}
