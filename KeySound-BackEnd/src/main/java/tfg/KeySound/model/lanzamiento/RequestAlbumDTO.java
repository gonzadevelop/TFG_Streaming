package tfg.KeySound.model.lanzamiento;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import tfg.KeySound.model.cancion.RequestCancionAlbumDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RequestAlbumDTO {
    private String nombreAlbum;
    private MultipartFile portada;
    private List<RequestCancionAlbumDTO> canciones;
    private LocalDateTime fechaLanzamiento;
}
