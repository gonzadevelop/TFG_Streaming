package tfg.KeySound.model.artista;

import lombok.Data;
import tfg.KeySound.model.album.ResponseAlbumDTO;
import tfg.KeySound.model.pista.ResponsePistaHomeDTO;

import java.util.List;

@Data
public class ResponseArtistaDTO {
    private Long id;
    private String username;
    private String urlAvatar;
    private int seguidores;
    private int cancionesEnFavoritos;
    private boolean sigueAlArtista;
    private List<ResponsePistaHomeDTO> canciones;
    private List<ResponseAlbumDTO> albums;
}
