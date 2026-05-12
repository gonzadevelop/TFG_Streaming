import { IPlaylist } from './home/IPlaylist';

export interface IResponseUsuario {
  username: string;
  email: string;
  biografia: string;
  urlAvatar: string;
  playlists: IPlaylist[];
}

export interface IUpdatePerfilRequest {
  biografia?: string;
  email?: string;
}


