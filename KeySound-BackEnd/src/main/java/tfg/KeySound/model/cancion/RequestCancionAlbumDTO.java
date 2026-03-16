package tfg.KeySound.model.cancion;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class RequestCancionAlbumDTO {
    private Long idCancionExistente; // Si se va a usar una canción ya existente, se proporciona SOLO!!!! su ID
    private String titulo;
    private MultipartFile archivo;
    private List<Long> idArtistas;
}
