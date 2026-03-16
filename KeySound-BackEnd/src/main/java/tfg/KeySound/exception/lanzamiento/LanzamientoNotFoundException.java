package tfg.KeySound.exception.lanzamiento;

public class LanzamientoNotFoundException extends RuntimeException {
    public LanzamientoNotFoundException(Long id) {
        super("El lanzamiento con id " + id + " no se ha encontrado.");
    }
}
