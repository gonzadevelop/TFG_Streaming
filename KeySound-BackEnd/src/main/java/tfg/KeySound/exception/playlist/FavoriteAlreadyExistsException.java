package tfg.KeySound.exception.playlist;

import org.springframework.http.HttpStatus;
import tfg.KeySound.exception.KeySoundException;

public class FavoriteAlreadyExistsException extends KeySoundException {
    public FavoriteAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "La canción ya está en favoritos." );
    }
}
