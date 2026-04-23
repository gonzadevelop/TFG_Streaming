package tfg.KeySound.model.home;

import lombok.Builder;
import lombok.Data;
import tfg.KeySound.model.album.ResponseAlbumDTO;
import tfg.KeySound.model.album.ResponseProximoAlbumDTO;
import tfg.KeySound.model.artista.ResponseArtistaHomeDTO;
import tfg.KeySound.model.pista.ResponsePistaHomeDTO;
import tfg.KeySound.model.playlist.ResponseKeySoundPlaylistDTO;

import java.util.List;

@Data
@Builder
public class HomeDTO {
    private List<ResponseKeySoundPlaylistDTO> keySoundPlaylists;
    private List<ResponseArtistaHomeDTO> artistasSeguidos;
    private List<ResponseAlbumDTO> novedadesDeLaSemana;
    private List<ResponseProximoAlbumDTO> proximosLanzmientos;
    private List<ResponsePistaHomeDTO> cancionesMasEscuchadas;
}
