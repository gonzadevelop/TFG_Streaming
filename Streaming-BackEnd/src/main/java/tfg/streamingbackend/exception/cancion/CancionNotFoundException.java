package tfg.streamingbackend.exception.cancion;

public class CancionNotFoundException extends RuntimeException {
    public CancionNotFoundException(Long id) {
        super("La cancion con id " + id + " no se ha encontrado.");
    }
}
