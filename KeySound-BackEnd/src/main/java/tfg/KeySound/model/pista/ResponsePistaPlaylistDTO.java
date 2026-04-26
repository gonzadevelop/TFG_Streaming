package tfg.KeySound.model.pista;

import lombok.Data;

import java.util.List;

@Data
public class ResponsePistaPlaylistDTO {
    private Long idPista;
    private String titulo;
    private String urlPortada;
    private String urlCancion;
    private List<String> artistas;
    private Long reproducciones;
    private int duracionSegundos;
}
