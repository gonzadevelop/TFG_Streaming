package tfg.KeySound.model.album;

import lombok.Data;
import tfg.KeySound.model.artista.MiniArtistaDTO;

import java.time.LocalDateTime;

@Data
public class ResponseProximoAlbumDTO {
    private Long id;
    private MiniArtistaDTO artista;
    private String titulo;
    private String urlPortada;
    private LocalDateTime fechaLanzamiento;
    private String tipo; // "álbum" o "sencillo"
}
