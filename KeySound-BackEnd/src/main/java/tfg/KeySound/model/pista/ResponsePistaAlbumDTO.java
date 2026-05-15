package tfg.KeySound.model.pista;
import lombok.Data;
import java.util.List;

@Data
public class ResponsePistaAlbumDTO {
    private Long id;          // ← AÑADIR ESTO
    private String titulo;
    private List<String> artistas;
    private String urlCancion;
    private Integer reproducciones;
    private Integer duracionSegundos;
    private Integer numeroPista;
}

