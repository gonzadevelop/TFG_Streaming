package tfg.KeySound.exception.playlist;

public class PlaylistNotFoundException extends RuntimeException {
    public PlaylistNotFoundException(Long id) {
        super("La playlist con id " + id + " no se ha encontrado.");
    }
}
