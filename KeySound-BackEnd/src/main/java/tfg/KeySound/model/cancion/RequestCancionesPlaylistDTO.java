package tfg.KeySound.model.cancion;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RequestCancionesPlaylistDTO {
    private Long playlistId;
    private List<Long> pistaIds;
}
