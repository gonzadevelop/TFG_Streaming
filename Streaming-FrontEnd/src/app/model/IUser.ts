import IRole from './IRole';

export default interface IUser {
  id: number;
  name: string;
  email: string;
  password?: string;
  tipoSuscripcion: 'free' | 'premium';
  avatarUrl?: string;
  biografia?: string;
  idRole: IRole;
}
