import ICancion from './ICancion';

export default interface ITopMusicalDiario {
  id: number;
  posicionDia: number;
  fecha: Date;
  reproduccionesDia: number;
  cancionId: ICancion;
}
