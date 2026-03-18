package tfg.KeySound.exception.auth;

import org.springframework.http.HttpStatus;
import tfg.KeySound.exception.KeySoundException;

public class EmailNotFoundException extends KeySoundException {
    public EmailNotFoundException(String email) {
        super(HttpStatus.NOT_FOUND, "El email '" + email + "' no se ha encontrado.");
    }
}
