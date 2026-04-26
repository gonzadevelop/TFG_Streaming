package tfg.KeySound.model.pista;

import lombok.Data;

import java.util.List;

@Data
public class ResponsePistaDTO {
    private String titulo;
    private List<String> artistas;
    private String urlCancion;
    private Long reproducciones;
    private int duracionSegundos;
    private int numeroPista;
}
