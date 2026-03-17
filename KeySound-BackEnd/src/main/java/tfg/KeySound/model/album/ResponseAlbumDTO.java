package tfg.KeySound.model.album;

import lombok.Data;

@Data
public class ResponseAlbumArtistaDTO {
    private Long id;
    private String titulo;
    private String urlPortada;
    private int anioLanzamiento;
    private String tipo; // "álbum" o "sencillo"
}
