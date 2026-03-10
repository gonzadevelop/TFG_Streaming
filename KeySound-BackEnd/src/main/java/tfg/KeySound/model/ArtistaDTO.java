package tfg.KeySound.model;

import lombok.Data;

import java.util.List;

@Data
public class ArtistaDTO {
    private String username;
    private List<CancionDTO> canciones;
    private List<LanzamientoDTO> lanzamientos;
    private int seguidores;
    private int cancionesEnFavoritos;
}
