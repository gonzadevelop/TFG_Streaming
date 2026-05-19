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

        // Minutos escuchados
        Long segundos = historialRepo.sumSegundosEscuchadosMes(usuarioId, desde, hasta);
        long minutos = (segundos != null ? segundos : 0L) / 60;

        // Top 5 canciones
        List<TopCancionDTO> topCanciones = historialRepo
                .findTop5CancionesMes(usuarioId, desde, hasta)
                .stream()
                .map(row -> {
                    Long cancionId = toLong(row[0]);
                    String artista = (String) row[6];
                    List<String> artistas = usuarioRepository.findArtistasDeCancion(cancionId);
                    if (artistas.isEmpty() && artista != null) artistas = List.of(artista);
                    return new TopCancionDTO(
                            cancionId,
                            (String) row[1],
                            toLong(row[3]),
                            row[2] != null ? ((Number) row[2]).intValue() : null,
                            firebaseService.obtenerUrlArchivoAudio((String) row[4]),
                            firebaseService.obtenerUrlArchivoImagen((String) row[5], (String) row[1]),
                            artista,
                            artista,
                            artistas
                    );
                })
                .toList();

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
        List<TopAlbumDTO> topAlbumes = historialRepo
                .findTop5AlbumesMes(usuarioId, desde, hasta)
                .stream()
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

        return new EstadisticasDTO(minutos, topCanciones, topArtistas, topAlbumes);
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
        return (segundos != null ? segundos : 0L) / 60;
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

        return historialRepo.findTop5CancionesMes(usuarioId, desde, hasta)
                .stream()
                .map(row -> {
                    Long cancionId = toLong(row[0]);
                    String artista = (String) row[6];
                    List<String> artistas = usuarioRepository.findArtistasDeCancion(cancionId);
                    if (artistas.isEmpty() && artista != null) artistas = List.of(artista);
                    return new TopCancionDTO(
                            cancionId,
                            (String) row[1],
                            toLong(row[3]),
                            row[2] != null ? ((Number) row[2]).intValue() : null,
                            firebaseService.obtenerUrlArchivoAudio((String) row[4]),
                            firebaseService.obtenerUrlArchivoImagen((String) row[5], (String) row[1]),
                            artista,
                            artista,
                            artistas
                    );
                })
                .toList();
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

        return historialRepo.findTop5AlbumesMes(usuarioId, desde, hasta)
                .stream()
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

