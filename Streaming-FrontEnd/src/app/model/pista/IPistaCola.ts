import { IPista } from './IPista';

export default interface IPistaCola extends IPista {
  reproduciendo: boolean;
  orden: number;
  esPrioridad?: boolean;
}
