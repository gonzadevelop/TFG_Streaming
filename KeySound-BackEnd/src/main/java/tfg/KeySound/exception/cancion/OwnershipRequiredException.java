package tfg.KeySound.exception.cancion;

public class OwnershipRequiredException extends RuntimeException {
    public OwnershipRequiredException() {
        super("No tienes permiso para eliminar esta canción.");
    }
}
