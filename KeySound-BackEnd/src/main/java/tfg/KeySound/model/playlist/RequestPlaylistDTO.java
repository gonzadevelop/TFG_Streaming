package tfg.KeySound.model.playlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestPlaylistDTO {
    private String nombrePlaylist;
    private Boolean esPublica;
    private String descripcion;
    private MultipartFile fotoPortada;
}
