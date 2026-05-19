package tfg.KeySound.model.estadisticas;

import java.util.List;

public record TopAlbumDTO(
        Long albumId,
        String titulo,
        String urlPortada,
        Long reproducciones,
        String artista,
        List<String> artistas
) {}

