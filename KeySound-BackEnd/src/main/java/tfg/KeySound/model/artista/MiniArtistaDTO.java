package tfg.KeySound.model.artista;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MiniArtistaDTO {
    private Long id;
    private String username;
}
