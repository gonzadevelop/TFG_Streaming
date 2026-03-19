package tfg.KeySound.model.album;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import tfg.KeySound.model.cancion.RequestCancionAlbumDTO;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RequestAlbumDTO {
    private String nombreAlbum;
    private List<RequestCancionAlbumDTO> canciones;
    private LocalDateTime fechaLanzamiento;
}
