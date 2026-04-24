package tfg.KeySound.model.pista;

import lombok.Data;
import tfg.KeySound.model.artista.MiniArtistaDTO;

import java.util.List;

@Data
public class ResponsePistaPlaylistDTO {
    private Long idPista;
    private String titulo;
    private List<MiniArtistaDTO> artistas;
    private String urlPortada;
    private String urlCancion;
    private int duracionSegundos;
}
