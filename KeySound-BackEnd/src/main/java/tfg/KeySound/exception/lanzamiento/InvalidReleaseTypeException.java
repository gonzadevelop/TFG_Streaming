package tfg.KeySound.exception.lanzamiento;

import org.springframework.http.HttpStatus;
import tfg.KeySound.exception.KeySoundException;

public class InvalidReleaseTypeException extends KeySoundException {
    public InvalidReleaseTypeException(Long idSencillo) {
        super(HttpStatus.BAD_REQUEST, "El lanzamiento con ID: " + idSencillo + " no es un sencillo");
    }
}
