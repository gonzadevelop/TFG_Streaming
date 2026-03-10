package tfg.KeySound.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class AddCancionesPlaylistDTO {
    private Long playlistId;
    private List<Long> lanzamientoCancionIds;
}
