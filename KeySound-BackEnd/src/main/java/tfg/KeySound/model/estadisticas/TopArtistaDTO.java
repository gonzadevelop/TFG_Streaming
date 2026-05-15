package tfg.KeySound.model.estadisticas;

public record TopArtistaDTO(
        Long artistaId,
        String nombre,
        Long reproducciones,
        String username
) {}
