import ICancion from './ICancion';

export default interface ICancionProductor {
  id: number;
  nombreProductor: string;
  cancionId: ICancion;
}
