package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.exception.auth.UsernameNotFoundException;
import tfg.KeySound.model.estadisticas.EstadisticasDTO;
import tfg.KeySound.model.estadisticas.TopAlbumDTO;
import tfg.KeySound.model.estadisticas.TopArtistaDTO;
import tfg.KeySound.model.estadisticas.TopCancionDTO;
import tfg.KeySound.repositorys.HistorialReproduccionesRepository;
import tfg.KeySound.repositorys.UsuarioRepository;
import tfg.KeySound.services.external.FirebaseService;
import tfg.KeySound.services.external.JwtService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadisticasService {

    private final HistorialReproduccionesRepository historialRepo;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final FirebaseService firebaseService;

    public EstadisticasDTO getEstadisticasMensuales(String token) {
        String username = jwtService.extractUsername(token);
        Long usuarioId = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username))
                .getId();

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime desde = ahora.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime hasta = desde.plusMonths(1);

        // Segundos totales escuchados (el frontend formateará h:min)
        Long segundos = historialRepo.sumSegundosEscuchadosMes(usuarioId, desde, hasta);
        long segundosTotales = segundos != null ? segundos : 0L;

        // Top 5 canciones
        List<TopCancionDTO> topCanciones = buildTopCanciones(
                historialRepo.findTop5CancionesMes(usuarioId, desde, hasta));

        // Top 5 artistas
        List<TopArtistaDTO> topArtistas = historialRepo
                .findTop5ArtistasMes(usuarioId, desde, hasta)
                .stream()
                .map(row -> new TopArtistaDTO(
                        toLong(row[0]),
                        (String) row[1],
                        toLong(row[2]),
                        (String) row[3]
                ))
                .toList();

        // Top 5 álbumes
        List<TopAlbumDTO> topAlbumes = buildTopAlbumes(
                historialRepo.findTop5AlbumesMes(usuarioId, desde, hasta));

        return new EstadisticasDTO(segundosTotales, topCanciones, topArtistas, topAlbumes);
    }

    public Long getMinutosMensuales(String token) {
        String username = jwtService.extractUsername(token);
        Long usuarioId = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username))
                .getId();

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime desde = ahora.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime hasta = desde.plusMonths(1);

        Long segundos = historialRepo.sumSegundosEscuchadosMes(usuarioId, desde, hasta);
        // Devolvemos segundos para que el frontend pueda formatear h:min correctamente
        return segundos != null ? segundos : 0L;
    }

    public Long getTotalReproduccionesMes(String token) {
        String username = jwtService.extractUsername(token);
        Long usuarioId = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username))
                .getId();

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime desde = ahora.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime hasta = desde.plusMonths(1);

        Long total = historialRepo.countReproduccionesMes(usuarioId, desde, hasta);
        return total != null ? total : 0L;
    }

    public List<TopCancionDTO> getTopCanciones(String token) {
        String username = jwtService.extractUsername(token);
        Long usuarioId = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username))
                .getId();

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime desde = ahora.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime hasta = desde.plusMonths(1);

        return buildTopCanciones(historialRepo.findTop5CancionesMes(usuarioId, desde, hasta));
    }

    public List<TopArtistaDTO> getTopArtistas(String token) {
        String username = jwtService.extractUsername(token);
        Long usuarioId = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username))
                .getId();

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime desde = ahora.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime hasta = desde.plusMonths(1);

        return historialRepo.findTop5ArtistasMes(usuarioId, desde, hasta)
                .stream()
                .map(row -> new TopArtistaDTO(
                        toLong(row[0]),
                        (String) row[1],
                        toLong(row[2]),
                        (String) row[3]
                ))
                .toList();
    }

    public List<TopAlbumDTO> getTopAlbumes(String token) {
        String username = jwtService.extractUsername(token);
        Long usuarioId = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username))
                .getId();

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime desde = ahora.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime hasta = desde.plusMonths(1);

        return buildTopAlbumes(historialRepo.findTop5AlbumesMes(usuarioId, desde, hasta));
    }

    // ---- Helpers privados ----

    /**
     * Construye la lista TopCancionDTO a partir de los rows de la query nativa.
     * row[0]=cancionId, row[1]=titulo, row[2]=duracionSegundos,
     * row[3]=reproducciones, row[4]=archivoCancion, row[5]=archivoPortada, row[6]=artistaId
     */
    private List<TopCancionDTO> buildTopCanciones(List<Object[]> rows) {
        return rows.stream()
                .map(row -> {
                    Long cancionId = toLong(row[0]);
                    List<String> artistas = usuarioRepository.findArtistasDeCancion(cancionId);
                    String artistaUsername = artistas.isEmpty() ? null : artistas.get(0);
                    return new TopCancionDTO(
                            cancionId,
                            (String) row[1],
                            toLong(row[3]),
                            row[2] != null ? ((Number) row[2]).intValue() : null,
                            firebaseService.obtenerUrlArchivoAudio((String) row[4]),
                            firebaseService.obtenerUrlArchivoImagen((String) row[5], (String) row[1]),
                            artistaUsername,
                            artistaUsername,
                            artistas
                    );
                })
                .toList();
    }

    /**
     * Construye la lista TopAlbumDTO a partir de los rows de la query nativa.
     * row[0]=albumId, row[1]=titulo, row[2]=archivoPortada, row[3]=reproducciones, row[4]=artista
     */
    private List<TopAlbumDTO> buildTopAlbumes(List<Object[]> rows) {
        return rows.stream()
                .map(row -> {
                    Long albumId = toLong(row[0]);
                    String artista = (String) row[4];
                    List<String> artistas = usuarioRepository.findArtistasDeAlbum(albumId);
                    if (artistas.isEmpty() && artista != null) artistas = List.of(artista);
                    return new TopAlbumDTO(
                            albumId,
                            (String) row[1],
                            firebaseService.obtenerUrlArchivoImagen((String) row[2], (String) row[1]),
                            toLong(row[3]),
                            artista,
                            artistas
                    );
                })
                .toList();
    }

    private Long toLong(Object value) {
        if (value == null) return 0L;
        return ((Number) value).longValue();
    }
}

