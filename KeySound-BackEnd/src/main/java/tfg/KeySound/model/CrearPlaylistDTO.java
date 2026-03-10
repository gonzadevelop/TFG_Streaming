package tfg.KeySound.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearPlaylistDTO {
    private String nombrePlaylist;
    private Boolean esPublica;
    private MultipartFile fotoPortada;
}
