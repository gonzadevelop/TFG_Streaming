package tfg.KeySound.model.cancion;

public record ResponseCancionExistenteDTO(
        Long idCancion,
        String titulo,
        String album,
        String caratula
) {}

