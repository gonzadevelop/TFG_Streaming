import { IKeySoundPlaylist } from './IKeySoundPlaylist';
import { IArtistaHome } from './IArtistaHome';
import { IAlbum } from './IAlbum';
import { IProximoAlbum } from './IProximoAlbum';
import { IPistaHome } from './IPistaHome';

export interface IHome {
  keySoundPlaylists: IKeySoundPlaylist[];
  artistasSeguidos: IArtistaHome[];
  novedadesDeLaSemana: IAlbum[];
  proximosLanzmientos: IProximoAlbum[];
  cancionesMasEscuchadas: IPistaHome[];
}
