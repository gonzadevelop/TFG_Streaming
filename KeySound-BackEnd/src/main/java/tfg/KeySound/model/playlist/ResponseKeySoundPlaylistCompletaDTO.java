package tfg.KeySound.model.playlist;

import lombok.Data;
import tfg.KeySound.model.pista.ResponsePistaTopPlaylistDTO;

import java.util.List;

@Data
public class ResponseKeySoundPlaylistCompletaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String urlPortada;
    private List<ResponsePistaTopPlaylistDTO> pistas;
    private boolean esPropia;
}
