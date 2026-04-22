package tfg.KeySound.model.playlist;

import lombok.Data;
import tfg.KeySound.model.pista.ResponsePistaPlaylistDTO;

import java.util.List;

@Data
public class ResponsePlaylistCompletaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String urlPortada;
    private List<ResponsePistaPlaylistDTO> pistas;
    private boolean esPropia;
}
