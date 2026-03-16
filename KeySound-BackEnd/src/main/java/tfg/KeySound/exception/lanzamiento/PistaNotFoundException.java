package tfg.KeySound.exception.lanzamiento;

public class PistaNotFoundException extends RuntimeException {
    public PistaNotFoundException(Long id) {
        super("Pista con id " + id + " no encontrado");
    }
}
