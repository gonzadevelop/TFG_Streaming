package tfg.streamingbackend.model;

import lombok.Data;

@Data
public class LanzamientoDTO {
    private Long id;
    private String titulo;
    private String urlPortada;
    private int anioLanzamiento;
    private String tipo; // "álbum" o "sencillo"
}
