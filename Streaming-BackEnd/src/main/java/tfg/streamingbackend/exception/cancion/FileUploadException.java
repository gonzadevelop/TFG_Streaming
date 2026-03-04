package tfg.streamingbackend.exception.cancion;

public class FileUploadException extends RuntimeException {
    public FileUploadException() {
        super("El archivo no se ha podido subir.");
    }
}
