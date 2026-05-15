export interface IAlbumUploadTrack {
  titulo: string;
  idArtistas: number[];
  idCancionExistente?: number;
}

export interface IAlbumUpload {
  nombreAlbum: string;
  fechaLanzamiento: string;
  canciones: IAlbumUploadTrack[];
}

