package tfg.KeySound.exception.lanzamiento;

public class RelationNotFoundException extends RuntimeException {
    public RelationNotFoundException() {
        super("Relación entre lanzamiento y canción no encontrada");
    }
}
