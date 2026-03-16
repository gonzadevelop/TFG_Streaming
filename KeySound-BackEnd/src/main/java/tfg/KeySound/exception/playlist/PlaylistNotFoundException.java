package tfg.KeySound.exception.playlist;

import org.springframework.http.HttpStatus;
import tfg.KeySound.exception.KeySoundException;

public class PlaylistNotFoundException extends KeySoundException {
    public PlaylistNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "La playlist con id " + id + " no se ha encontrado.");
    }
}
