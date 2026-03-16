package tfg.KeySound.exception.lanzamiento;

import tfg.KeySound.exception.KeySoundException;
import org.springframework.http.HttpStatus;

public class RelationNotFoundException extends KeySoundException {
    public RelationNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Relación entre lanzamiento y canción no encontrada");
    }
}
