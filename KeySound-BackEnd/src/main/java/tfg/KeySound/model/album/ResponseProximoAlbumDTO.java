package tfg.KeySound.model.album;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseProximoAlbumDTO {
    private Long id;
    private String titulo;
    private String urlPortada;
    private LocalDateTime fechaLanzamiento;
    private String tipo; // "álbum" o "sencillo"
}
