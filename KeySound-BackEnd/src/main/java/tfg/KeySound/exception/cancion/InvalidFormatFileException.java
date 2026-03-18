package tfg.KeySound.exception.cancion;

import org.springframework.http.HttpStatus;
import tfg.KeySound.exception.KeySoundException;

public class InvalidFormatFileException extends KeySoundException {
    public InvalidFormatFileException(String formato) {
        super(HttpStatus.BAD_REQUEST, "El formato de archivo " + formato + " no es válido. Solo se permiten archivos de audio con formato MP3 o WAV.");
    }
}
