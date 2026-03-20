package tfg.KeySound.exception.playlist;

import org.springframework.http.HttpStatus;
import tfg.KeySound.exception.KeySoundException;

public class OwnershipRequiredException extends KeySoundException {
    public OwnershipRequiredException() {
        super(HttpStatus.FORBIDDEN, "No tienes permiso para eliminar esta playlist.");
    }
}
