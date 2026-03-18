package tfg.KeySound.model.home;

import lombok.Data;
import tfg.KeySound.model.album.ResponseAlbumDTO;
import tfg.KeySound.model.artista.ResponseArtistaHomeDTO;
import tfg.KeySound.model.pista.ResponsePistaHomeDTO;
import tfg.KeySound.model.playlist.ResponseKeySoundPlaylistDTO;
import tfg.KeySound.model.playlist.ResponsePlaylistDTO;

import java.util.List;

@Data
public class HomeDTO {
    private List<ResponseKeySoundPlaylistDTO> keySoundPlaylists;
    private List<ResponseArtistaHomeDTO> artistasSeguidos;
    private List<ResponseAlbumDTO> albumsSeguidos;
    private List<ResponseAlbumDTO> proximosLanzmientos;
    private List<ResponsePistaHomeDTO> cancionesMasEscuchadas;
}
