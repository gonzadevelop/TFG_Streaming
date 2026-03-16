package tfg.KeySound.exception.pista;

import tfg.KeySound.exception.KeySoundException;
import org.springframework.http.HttpStatus;

public class PistaNotFoundException extends KeySoundException {
    public PistaNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "Pista con id " + id + " no encontrado");
    }
}
