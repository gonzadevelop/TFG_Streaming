package tfg.KeySound.model;

import lombok.Data;

import java.util.List;

@Data
public class CancionDTO {
    private Long idLanzamiento;
    private String titulo;
    private String urlPortada;
    private List<String> artistas;
    private String urlCancion;
    private Long reproducciones;
}
