package tfg.KeySound.model.playlist;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponsePlaylistDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String urlPortada;
}
