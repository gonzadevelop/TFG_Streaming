package tfg.KeySound.model.favoritos;

import lombok.AllArgsConstructor;
import lombok.Data;
import tfg.KeySound.model.pista.ResponsePistaPlaylistDTO;

import java.util.List;

@Data
@AllArgsConstructor
public class ResponseFavoritosDTO {
    private List<ResponsePistaPlaylistDTO> pistas;
}

