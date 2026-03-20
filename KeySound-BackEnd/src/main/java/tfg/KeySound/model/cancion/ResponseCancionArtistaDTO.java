package tfg.KeySound.model.cancion;

import lombok.Data;

import java.util.List;

@Data
public class ResponseCancionArtistaDTO { // Implementar segundos...
    private Long idAlbum;
    private String titulo;
    private String urlPortada;
    private List<String> artistas;
    private String urlCancion;
    private Long reproducciones;
}
