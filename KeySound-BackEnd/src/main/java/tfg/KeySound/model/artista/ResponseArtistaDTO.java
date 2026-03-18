package tfg.KeySound.model.artista;

import lombok.Data;
import tfg.KeySound.model.cancion.ResponseCancionArtistaDTO;
import tfg.KeySound.model.album.ResponseAlbumDTO;

import java.util.List;

@Data
public class ResponseArtistaDTO {
    private String username;
    private String urlAvatar;
    private int seguidores;
    private int cancionesEnFavoritos;
    private List<ResponseCancionArtistaDTO> canciones;
    private List<ResponseAlbumDTO> albums;
}
