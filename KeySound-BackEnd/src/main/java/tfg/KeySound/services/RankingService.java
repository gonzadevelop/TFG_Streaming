package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.entitys.*;
import tfg.KeySound.mappers.ArtistaMapper;
import tfg.KeySound.mappers.PlaylistMapper;
import tfg.KeySound.mappers.TopMusicalDiarioMapper;
import tfg.KeySound.model.pista.ResponsePistaPlaylistDTO;
import tfg.KeySound.model.playlist.ResponsePlaylistCompletaDTO;
import tfg.KeySound.repositorys.*;
import tfg.KeySound.services.external.FirebaseService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RankingService {

    /**
     * Inyección de dependencias de los repositorios necesarios para acceder a los datos de canciones y reproducciones.
     */
    private final TopMusicalDiarioRepository topMusicalDiarioRepository;
    private final HistorialReproduccionesRepository historialReproduccionesRepository;
    private final TopMusicalDiarioMapper topMusicalDiarioMapper;
    private final CancionRepository cancionRepository;
    private final FirebaseService firebaseService;
    private final ArtistaMapper artistaMapper;
    private final PlaylistRepository playlistRepository;
    private final PlaylistMapper playlistMapper;
    /**
     * Metodo para obtener el ranking diario actual.
     * Si no se proporciona una fecha, se devuelve el ranking del día anterior (ya que el ranking se actualiza a las 00:00).
     */
    public ResponsePlaylistCompletaDTO getDailyTop30(String fecha) {
        LocalDate fechaConsulta = fecha == null || fecha.isEmpty()
                ? LocalDate.now().minusDays(1)
                : LocalDate.parse(fecha);

        List<ResponsePistaPlaylistDTO> pistas = topMusicalDiarioMapper.toDtos(topMusicalDiarioRepository.findByFecha(fechaConsulta));

        pistas
                .stream()
                .peek(
                        r-> {
                            Cancion cancion = cancionRepository.findById(r.getIdPista()).orElseThrow();

                            // obtener usuario principal del album (si existe) y mapear a MiniArtistaDTO
                            List<String> artistas = Stream.concat(
                                            cancion
                                                    .getPistas()
                                                    .stream()
                                                    .limit(1)
                                                    .map(pista -> pista.getAlbum().getUsuario().getUsername()),
                                            cancion
                                                    .getUsuarios()
                                                    .stream()
                                                    .map(Usuario::getUsername)
                                    )
                                    .toList();
                            r.setIdPista(cancion.getPistas().stream().findFirst().get().getId());
                            r.setArtistas(artistas);
                            r.setUrlPortada(cancion.getPistas().stream().findFirst().map(p -> firebaseService.obtenerUrlArchivoImagen(p.getAlbum().getArchivoPortada(), "")).orElse(null));
                            r.setUrlCancion(firebaseService.obtenerUrlArchivoAudio(cancion.getArchivoCancion()));
                        }
                )
                .toList();

        Playlist entity = playlistRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("No se encontró la playlist de ranking diario"));

        String imagen = firebaseService.obtenerUrlArchivoImagen(entity.getFotoPortada(), "");

        return playlistMapper.toDto(entity, imagen, pistas);
    }

    /**
     * Metodo que calcula el top 30 de canciones más escuchadas en las últimas 24 horas y actualiza la tabla correspondiente.
     */
    public void updateDailyTop30() {
        List<TopMusicalDiario> topMusicalDiario = historialReproduccionesRepository.findTop30ByFecha(LocalDate.now().minusDays(1));

        topMusicalDiarioRepository.saveAll(topMusicalDiario);
    }

    /**
     * Metodo para verificar si el ranking diario está desactualizado (es decir, si el último ranking no corresponde al día actual).
     * @return true si el ranking está desactualizado, false si está actualizado.
     */
    public boolean isRankingOutdated() {
        // Obtener la fecha del último ranking
        LocalDate lastRankingDate = topMusicalDiarioRepository
                .findTopByOrderByFechaDesc()
                .getFecha();

        // comprobar si la fecha del último ranking es anterior a la fecha actual
        return !lastRankingDate.isEqual(LocalDate.now().minusDays(1));
    }
}
