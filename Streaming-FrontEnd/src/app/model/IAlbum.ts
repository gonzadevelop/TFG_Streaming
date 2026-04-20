import IUser from './IUser';

export interface IAlbum {
  id: number;
  archivo_portada?: string;
  fecha_lanzamiento: Date;
  tipo: "sencillo" | "album";
  titulo: string;
  usuario_id: IUser;
  es_borrador: boolean;
}
