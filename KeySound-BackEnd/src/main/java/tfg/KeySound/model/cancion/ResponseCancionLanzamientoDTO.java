package tfg.KeySound.model.cancion;

import lombok.Data;

import java.util.List;

@Data
public class ResponseCancionLanzamientoDTO {
    private String titulo;
    private List<String> artistas;
    private String urlCancion;
    private Long reproducciones;
    private int duracionSegundos;
    private int numeroPista;
}
