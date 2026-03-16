package tfg.KeySound.exception.artista;

import org.springframework.http.HttpStatus;
import tfg.KeySound.exception.KeySoundException;

public class FollowRestrictionException extends KeySoundException {
    public FollowRestrictionException() {
        super(HttpStatus.FORBIDDEN, "Los artistas no pueden seguir a otros usuarios");
    }
}
