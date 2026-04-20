import ICancion from './ICancion';
import IUser from './IUser';

export default interface IHistorialReproducciones {
  id: number;
  fechaReproduccion: Date;
  cancionId: ICancion;
  usuarioId: IUser;
}
