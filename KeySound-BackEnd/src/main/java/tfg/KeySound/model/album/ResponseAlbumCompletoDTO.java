package tfg.KeySound.model.album;

import lombok.Builder;
import lombok.Data;
import tfg.KeySound.model.pista.ResponsePistaDTO;

import java.util.List;

@Data
@Builder
public class ResponseAlbumCompletoDTO {
    private String titulo;
    private String portada;
    private String artista;
    private int anioLanzamiento;
    private int duracionTotalSegundos;
    private int numCanciones;
    private String tipo;
    private List<ResponsePistaDTO> canciones;
}
