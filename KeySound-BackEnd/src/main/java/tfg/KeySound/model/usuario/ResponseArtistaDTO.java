package tfg.KeySound.model.usuario;

import lombok.Data;
import tfg.KeySound.model.cancion.ResponseCancionArtistaDTO;
import tfg.KeySound.model.lanzamiento.ResponseLanzamientoArtistaDTO;

import java.util.List;

@Data
public class ResponseArtistaDTO {
    private String username;
    private List<ResponseCancionArtistaDTO> canciones;
    private List<ResponseLanzamientoArtistaDTO> lanzamientos;
    private int seguidores;
    private int cancionesEnFavoritos;
}
