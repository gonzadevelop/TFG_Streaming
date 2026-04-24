import { IKeySoundPlaylist } from './IKeySoundPlaylist';
import { IArtistaHome } from './IArtistaHome';
import { IAlbum } from './IAlbum';
import { IProximoAlbum } from './IProximoAlbum';
import {IPistaPlaylist} from '../pista/IPistaPlaylist';

export interface IHome {
  keySoundPlaylists: IKeySoundPlaylist[];
  artistasSeguidos: IArtistaHome[];
  novedadesDeLaSemana: IAlbum[];
  proximosLanzmientos: IProximoAlbum[];
  cancionesMasEscuchadas: IPistaPlaylist[];
}
