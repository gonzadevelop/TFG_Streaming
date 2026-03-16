import IUser from './IUser';

export default interface IPlaylist {
  id: number;
  esPublica: boolean;
  nombre: string;
  propietario_id: IUser;
  descripción?: string;
  fecha_creacion: Date;
  foto_portada?: string;
}
