import IUser from './IUser';
import IPista from './IPista';

export default interface IFavorito {
  idUsuario: IUser;
  idPista: IPista;
  fechaGuardado?: Date;
}
