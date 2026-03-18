package tfg.KeySound.exception.auth;

import org.springframework.http.HttpStatus;
import tfg.KeySound.exception.KeySoundException;

public class EmailAlrreadyExistsException extends KeySoundException {
    public EmailAlrreadyExistsException(String email) {
        super(HttpStatus.CONFLICT, "El email " + email + " ya está registrado.");
    }
}
