package tfg.KeySound.model.playlist;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseKeySoundPlaylistDTO {
    private String urlPlaylist;
    private String nombre;
    private String descripcion;
    private String urlPortada;
}
