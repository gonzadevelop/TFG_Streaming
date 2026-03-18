package tfg.KeySound.exception.usuario;

import org.springframework.http.HttpStatus;
import tfg.KeySound.exception.KeySoundException;

public class AlreadyFollowingException extends KeySoundException {
    public AlreadyFollowingException() {
        super(HttpStatus.CONFLICT, "Ya sigues a este usuario");
    }
}
