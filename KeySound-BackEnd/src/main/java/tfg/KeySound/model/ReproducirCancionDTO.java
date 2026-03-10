package tfg.KeySound.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReproducirCancionDTO {
    String nombreCancion;
    List<String> artistas;
    String urlAudio;
    String urlPortada;
}
