import IUser from './IUser';

export default interface ILanzamiento {
  id: number;
  archivo_portada: string
  fecha_lanzamiento: Date;
  tipo: "sencillo" | "ep" | "álbum";
  titulo: string;
  idUsuario: IUser
}
