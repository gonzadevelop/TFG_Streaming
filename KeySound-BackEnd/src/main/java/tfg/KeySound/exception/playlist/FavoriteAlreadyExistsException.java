package tfg.KeySound.exception.playlist;

public class FavoriteAlreadyExistsException extends RuntimeException {
    public FavoriteAlreadyExistsException() {
        super("La canción ya está en favoritos." );
    }
}
