package tfg.KeySound.model.estadisticas;

public record TopCancionDTO(
        Long cancionId,
        String titulo,
        Long reproducciones,
        Integer duracionSegundos,
        String urlCancion,
        String urlPortada,
        String artista
) {}

