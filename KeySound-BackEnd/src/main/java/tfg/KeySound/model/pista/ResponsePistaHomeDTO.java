package tfg.KeySound.model.pista;

import lombok.Data;
import tfg.KeySound.model.artista.MiniArtistaDTO;

import java.util.List;

@Data
public class ResponsePistaHomeDTO {
    private Long idPista;
    private Long albumId;
    private String titulo;
    private String urlPortada;
    private String urlCancion;
    private List<MiniArtistaDTO> artistas;
    private int duracionSegundos;
    private int reproduccionesDelUsuario;
}
