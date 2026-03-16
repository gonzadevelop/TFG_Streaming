import ICancion from './ICancion';
import ILanzamiento from './ILanzamiento';

export default interface IPista {
  id: number;
  numero_pista: number;
  idCancion: ICancion;
  idLanzamiento: ILanzamiento;
}
