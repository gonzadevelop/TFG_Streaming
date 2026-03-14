package tfg.KeySound.model.usuario;

import lombok.Data;
import tfg.KeySound.model.playlist.ResponsePlaylistDTO;

import java.util.List;

@Data
public class ResponseUsuarioDTO {
    private String username;
    private String email;
    private String biografia;
    private String urlAvatar;
    private List<ResponsePlaylistDTO> playlists;
}
