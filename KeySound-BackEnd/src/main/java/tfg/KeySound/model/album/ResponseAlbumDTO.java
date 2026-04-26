package tfg.KeySound.model.album;

import lombok.Data;

@Data
public class ResponseAlbumDTO {
    private Long id;
    private String artista;
    private String titulo;
    private String urlPortada;
    private int anioLanzamiento;
    private String tipo; // "álbum" o "sencillo"
}
