package tfg.KeySound.model.estadisticas;

import java.util.List;

public record EstadisticasDTO(
        Long minutosEscuchadosMes,
        List<TopCancionDTO> topCanciones,
        List<TopArtistaDTO> topArtistas,
        List<TopAlbumDTO> topAlbumes
) {}

