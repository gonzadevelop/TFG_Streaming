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

export interface IUserRegister {
  nombre: string;
  apellidos: string;
  username: string;
  email: string;
  password?: string;
  confirmPassword: string;
  genero: 'Masculino' | 'Femenino' | 'Otro' | 'Prefiero no decirlo';
  dni: string;
  telefono: number;
}

export interface IUserLogin {
  email: string;
  password?: string;
}
