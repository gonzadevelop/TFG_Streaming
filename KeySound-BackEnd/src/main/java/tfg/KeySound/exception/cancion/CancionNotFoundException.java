package tfg.KeySound.exception.cancion;

import org.springframework.http.HttpStatus;
import tfg.KeySound.exception.KeySoundException;


public class CancionNotFoundException extends KeySoundException {
    public CancionNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "La cancion con id " + id + " no se ha encontrado.");
    }
}
