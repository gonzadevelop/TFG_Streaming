import IRole from './IRole';

export default interface IUser {
  id: number;
  avatarUrl?: string;
  biografia?: string;
  email: string;
  password?: string;
  tipoSuscripcion: 'free' | 'premium';
  username: string;
  idRole: IRole;
}
