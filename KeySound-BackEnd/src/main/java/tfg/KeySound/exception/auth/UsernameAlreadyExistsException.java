package tfg.KeySound.exception.auth;

import org.springframework.http.HttpStatus;
import tfg.KeySound.exception.KeySoundException;

public class UsernameAlreadyExistsException extends KeySoundException {
    public UsernameAlreadyExistsException(String username) {
        super(HttpStatus.CONFLICT, "El nombre de usuario " + username + " ya está registrado.");
    }
}
