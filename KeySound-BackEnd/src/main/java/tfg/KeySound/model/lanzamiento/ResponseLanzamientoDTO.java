package tfg.KeySound.model.lanzamiento;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import tfg.KeySound.model.cancion.ResponseCancionLanzamientoDTO;

import java.util.List;

@Data
@Builder
public class ResponseLanzamientoDTO {
    private String nombreLanzamiento;
    private MultipartFile portada;
    private int anioLanzamiento;
    private int duracionTotalSegundos;
    private int numCanciones;
    private String tipo;
    private List<ResponseCancionLanzamientoDTO> canciones;
}
