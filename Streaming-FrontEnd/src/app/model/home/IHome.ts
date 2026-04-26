import { IPlaylist } from './IPlaylist';
import { IArtistaHome } from './IArtistaHome';
import { IAlbum } from './IAlbum';
import { IProximoAlbum } from './IProximoAlbum';
import {IPistaPlaylist} from '../pista/IPistaPlaylist';

export interface IHome {
  keySoundPlaylists: IPlaylist[];
  artistasSeguidos: IArtistaHome[];
  novedadesDeLaSemana: IAlbum[];
  proximosLanzmientos: IProximoAlbum[];
  cancionesMasEscuchadas: IPistaPlaylist[];
}
