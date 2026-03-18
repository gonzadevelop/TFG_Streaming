package tfg.KeySound.exception.album;

import org.springframework.http.HttpStatus;
import tfg.KeySound.exception.KeySoundException;

public class AlbumNotFoundException extends KeySoundException {
    public AlbumNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "El album con id " + id + " no se ha encontrado.");
    }
}
