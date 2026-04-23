export interface IPlaylistRequest {
  nombre: string;
  esPublica: boolean;
  descripcion?: string;
  fotoPortada?: File; // opcional, multipart/form-data
}

export interface ICancionesPlaylistRequest {
  playlistId: number;
  pistaIds: number[];
}

