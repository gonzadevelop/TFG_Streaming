package tfg.KeySound.model.album;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ResponseMiAlbumDTO {
    private Long id;
    private String titulo;
    private String portada;
    private String tipo;
    private boolean esBorrador;
    private LocalDate fechaLanzamiento;
    private int numeroCanciones;
}
