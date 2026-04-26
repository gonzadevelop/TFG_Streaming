package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.model.home.HomeDTO;

@Service
@RequiredArgsConstructor
public class HomeService {


    /**
     * Inyecciones por constructor
     */
    private final PlaylistService playlistService;
    private final ArtistaService artistaService;
    private final AlbumService albumService;
    private final CancionService cancionService;

    /**
     * Metodos llamados por endpoints
     */
    public HomeDTO getHome(String token) {
        return HomeDTO
                .builder()
                .keySoundPlaylists(playlistService.getPlaylists())
                .artistasSeguidos(artistaService.obtenerArtistasQueSigo(token))
                .novedadesDeLaSemana(albumService.obtenerNovedades())
                .proximosLanzmientos(albumService.obtenerProximosLanzamientos())
                .cancionesMasEscuchadas(cancionService.obtenerMisCancionesMasReproducidas(token))
                .build();
    }
}
