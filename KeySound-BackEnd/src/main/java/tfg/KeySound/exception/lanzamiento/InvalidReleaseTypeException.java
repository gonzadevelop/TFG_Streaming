package tfg.KeySound.exception.lanzamiento;

public class InvalidReleaseTypeException extends RuntimeException {
    public InvalidReleaseTypeException(Long idSencillo) {
        super("El lanzamiento con ID: " + idSencillo + " no es un sencillo");
    }
}
