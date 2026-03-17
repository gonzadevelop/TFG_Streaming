package tfg.KeySound.model.lanzamiento;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ResponseMiLanzamientoDTO {
    private Long idLanzamiento;
    private String nombreAlbum;
    private String portada;
    private String tipo;
    private boolean esBorrador;
    private LocalDate fechaLanzamiento;
    private int numeroCanciones;
}
