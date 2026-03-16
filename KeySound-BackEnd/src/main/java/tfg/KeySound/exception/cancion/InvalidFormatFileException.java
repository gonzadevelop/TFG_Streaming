package tfg.KeySound.exception.cancion;

public class InvalidFormatFileException extends RuntimeException {
    public InvalidFormatFileException(String formato) {
        super("El formato de archivo " + formato + " no es válido. Solo se permiten archivos de audio con formato MP3 o WAV.");
    }
}
