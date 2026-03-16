package tfg.KeySound.model.lanzamiento;

import lombok.Data;

@Data
public class ResponseLanzamientoArtistaDTO {
    private Long id;
    private String titulo;
    private String urlPortada;
    private int anioLanzamiento;
    private String tipo; // "álbum" o "sencillo"
}
