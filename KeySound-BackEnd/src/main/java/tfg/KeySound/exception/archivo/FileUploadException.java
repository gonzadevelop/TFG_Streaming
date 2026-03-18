package tfg.KeySound.exception.archivo;

import org.springframework.http.HttpStatus;
import tfg.KeySound.exception.KeySoundException;

public class FileUploadException extends KeySoundException {
    public FileUploadException() {
        super(HttpStatus.BAD_REQUEST, "El archivo no se ha podido subir a firebase.");
    }
}
