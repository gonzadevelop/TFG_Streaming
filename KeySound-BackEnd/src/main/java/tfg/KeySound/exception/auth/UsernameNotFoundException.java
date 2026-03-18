package tfg.KeySound.exception.auth;

import org.springframework.http.HttpStatus;
import tfg.KeySound.exception.KeySoundException;

public class UsernameNotFoundException extends KeySoundException {
    public UsernameNotFoundException(String username) {
        super(HttpStatus.NOT_FOUND, "El username " + username + " no se ha encontrado.");
    }
}
