import { IPlaylist } from './IPlaylist';
import { IArtistaHome } from './IArtistaHome';
import { IAlbum } from '../album/IAlbum';
import { IProximoAlbum } from './IProximoAlbum';
import {IPista} from '../pista/IPista';

export interface IHome {
  keySoundPlaylists: IPlaylist[];
  artistasSeguidos: IArtistaHome[];
  misPlaylist: IPlaylist[];
  novedadesDeLaSemana: IAlbum[];
  proximosLanzmientos: IProximoAlbum[];
  cancionesMasEscuchadas: IPista[];
}
