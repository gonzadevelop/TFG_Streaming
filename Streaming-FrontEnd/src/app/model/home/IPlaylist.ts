export interface IPlaylist {
  id: number;
  nombre: string;
  descripcion: string;
  urlPortada: string;
  /** Indica si la playlist es pública (puede ser undefined en respuestas antiguas del servidor) */
  esPublica?: boolean;
}
