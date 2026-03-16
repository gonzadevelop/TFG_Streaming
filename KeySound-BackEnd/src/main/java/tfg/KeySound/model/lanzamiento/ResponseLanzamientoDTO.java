package tfg.KeySound.model.lanzamiento;

import lombok.Builder;
import lombok.Data;
import tfg.KeySound.model.pista.ResponsePistaDTO;

import java.util.List;

@Data
@Builder
public class ResponseLanzamientoDTO {
    private String nombreLanzamiento;
    private String portada;
    private String artista;
    private int anioLanzamiento;
    private int duracionTotalSegundos;
    private int numCanciones;
    private String tipo;
    private List<ResponsePistaDTO> canciones;
}
