package tfg.KeySound.model.pista;

import lombok.Data;

import java.util.List;

@Data
public class ResponsePistaHomeDTO {
    private Long idPista;
    private Long idAlbum;
    private String titulo;
    private String urlPortada;
    private String urlCancion;
    private List<String> artistas;
    private int duracionSegundos;
    private int reproducciones;
}
