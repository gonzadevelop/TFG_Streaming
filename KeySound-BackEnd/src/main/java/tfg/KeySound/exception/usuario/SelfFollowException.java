package tfg.KeySound.exception.usuario;

import org.springframework.http.HttpStatus;
import tfg.KeySound.exception.KeySoundException;

public class SelfFollowException extends KeySoundException {
    public SelfFollowException() {
        super(HttpStatus.BAD_REQUEST, "No puedes seguirte a ti mismo");
    }
}
