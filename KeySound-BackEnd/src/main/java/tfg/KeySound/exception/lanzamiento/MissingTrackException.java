package tfg.KeySound.exception.lanzamiento;

import org.springframework.http.HttpStatus;
import tfg.KeySound.exception.KeySoundException;

public class MissingTrackException extends KeySoundException {
    public MissingTrackException(Long idSencillo) {
        super(HttpStatus.BAD_REQUEST, "No se encontró la canción asociada al lanzamiento con ID: " + idSencillo);
    }
}
