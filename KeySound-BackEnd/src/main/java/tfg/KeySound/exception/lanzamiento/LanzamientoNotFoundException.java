package tfg.KeySound.exception.lanzamiento;

import org.springframework.http.HttpStatus;
import tfg.KeySound.exception.KeySoundException;

public class LanzamientoNotFoundException extends KeySoundException {
    public LanzamientoNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "El lanzamiento con id " + id + " no se ha encontrado.");
    }
}
