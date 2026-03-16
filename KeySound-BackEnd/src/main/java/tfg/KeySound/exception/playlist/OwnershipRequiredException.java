package tfg.KeySound.exception.playlist;

public class OwnershipRequiredException extends RuntimeException {
    public OwnershipRequiredException() {
        super("No tienes permiso para eliminar esta playlist.");
    }
}
