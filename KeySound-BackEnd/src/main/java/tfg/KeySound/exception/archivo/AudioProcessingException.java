package tfg.KeySound.exception.archivo;

import org.springframework.http.HttpStatus;
import tfg.KeySound.exception.KeySoundException;

public class AudioProcessingException extends KeySoundException {
    public AudioProcessingException(String message) {
        super(HttpStatus.BAD_REQUEST ,"Error al procesaro el archivo de audio: " + message);
    }
}
