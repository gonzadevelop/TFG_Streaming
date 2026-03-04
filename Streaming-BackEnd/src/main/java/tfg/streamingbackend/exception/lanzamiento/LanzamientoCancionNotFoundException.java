package tfg.streamingbackend.exception.lanzamiento;

public class LanzamientoCancionNotFoundException extends RuntimeException {
    public LanzamientoCancionNotFoundException(Long id) {
        super("LanzamientoCancion con id " + id + " no encontrado");
    }
}

