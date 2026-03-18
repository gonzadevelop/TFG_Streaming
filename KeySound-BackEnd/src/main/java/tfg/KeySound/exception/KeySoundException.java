package tfg.KeySound.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Excepción base personalizada para KeySound que incluye un código de estado HTTP.
 * Todas las excepciones personalizadas de KeySound deben extender esta clase.
 */
@Getter
public abstract class KeySoundException extends RuntimeException {
    private final HttpStatus status;

    public KeySoundException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
