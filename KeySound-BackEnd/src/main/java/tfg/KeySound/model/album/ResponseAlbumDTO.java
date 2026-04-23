package tfg.KeySound.model.album;

import lombok.Data;
import tfg.KeySound.model.artista.MiniArtistaDTO;

@Data
public class ResponseAlbumDTO {
    private Long id;
    private MiniArtistaDTO artista;
    private String titulo;
    private String urlPortada;
    private int anioLanzamiento;
    private String tipo; // "álbum" o "sencillo"
}
