package tfg.KeySound.model.estadisticas;

import java.util.List;

public record TopCancionDTO(
        Long cancionId,
        String titulo,
        Long reproducciones,
        Integer duracionSegundos,
        String urlCancion,
        String urlPortada,
        String artista,
        String artistaUsername,
        List<String> artistas
) {}

