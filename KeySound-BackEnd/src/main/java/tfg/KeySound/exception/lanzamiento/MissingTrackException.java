package tfg.KeySound.exception.lanzamiento;

public class MissingTrackException extends RuntimeException {
    public MissingTrackException(Long idSencillo) {
        super("No se encontró la canción asociada al lanzamiento con ID: " + idSencillo);
    }
}
