package tfg.KeySound.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class CrearAlbumDTO {
    private String nombreAlbum;
    private MultipartFile portada;
    private List<CancionAlbumDTO> canciones;
}
