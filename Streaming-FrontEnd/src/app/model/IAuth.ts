import IUser from './IUser';

export interface IAuthResponse {
  token: string;
  user: IUser;
}

export interface ICheckEmailResponse {
  registered: boolean;
}
