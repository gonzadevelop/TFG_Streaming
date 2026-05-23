package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tfg.KeySound.entitys.*;
import tfg.KeySound.entitys.embeddedids.PlaylistPistaId;
import tfg.KeySound.exception.auth.UsernameNotFoundException;
import tfg.KeySound.exception.playlist.OwnershipRequiredException;
import tfg.KeySound.exception.playlist.PlaylistNotFoundException;
import tfg.KeySound.mappers.PlaylistMapper;
import tfg.KeySound.model.cancion.RequestCancionesPlaylistDTO;
import tfg.KeySound.model.playlist.RequestPlaylistDTO;
import tfg.KeySound.model.playlist.ResponsePlaylistCompletaDTO;
import tfg.KeySound.model.playlist.ResponsePlaylistDTO;
import tfg.KeySound.repositorys.*;
import tfg.KeySound.services.external.FirebaseService;
import tfg.KeySound.services.external.JwtService;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaylistService {

    /**
     * Inyecciones por constructor
     */
    private final JwtService jwtService;
    private final FirebaseService firebaseService;
    private final RankingService rankingService;

    private final PistaRepository pistaRepository;

    private final PlaylistPistaRepository playlistPistaRepository;
    private final PlaylistRepository playlistRepository;
    private final UsuarioRepository usuarioRepository;

    private final PlaylistMapper playlistMapper;

    /**
     * Metodos llamados por endpoints
     */
    public void crearPlaylist(RequestPlaylistDTO dto, String token) {

        // Extraer el nombre de usuario del token JWT
        String username = jwtService.extractUsername(token);

        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        // Mapear el DTO a la entidad Playlist (el mapper gestiona la foto de portada)
        Playlist playlist = playlistMapper.toEntity(dto, usuario);

        // Guardar la nueva playlist en la base de datos
        playlistRepository.save(playlist);
    }

    public void agregarCancionesAPlaylist(RequestCancionesPlaylistDTO dto, String token) {
        // Extraer el nombre de usuario del token JWT
        String username = jwtService.extractUsername(token);

        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        // Buscar la playlist por su ID y verificar que pertenece al usuario
        Playlist playlist = playlistRepository.findById(dto.getPlaylistId())
                .orElseThrow(() -> new PlaylistNotFoundException(dto.getPlaylistId()));

        // Verificar que el usuario es el propietario de la playlist
        if (!playlist.getPropietario().getId().equals(usuario.getId())) {
            throw new OwnershipRequiredException();
        }

        // Obtenemos todas las pistas
        List<Pista> pistasNuevas = pistaRepository.findAllById(dto.getPistaIds());

        // Validar si faltan pistas
        if (pistasNuevas.size() != dto.getPistaIds().size()) {
            throw new RuntimeException("Algunas pistas no fueron encontradas");
        }

        // Obtener IDs de pistas que están en la playlist para evitar duplicados
        Set<Long> pistasExistentesIds = playlistPistaRepository.findPistaIdsByPlaylistId(playlist.getId());

        // Crear nuevas relaciones PlaylistPista solo para las pistas que no están ya en la playlist
        pistasNuevas.stream()
                .filter(pista -> !pistasExistentesIds.contains(pista.getId()))
                .forEach(pista -> {
                    PlaylistPista relacion = new PlaylistPista();
                    relacion.setId(new PlaylistPistaId(playlist.getId(), pista.getId()));
                    relacion.setPlaylist(playlist);
                    relacion.setPista(pista);
                    playlist.getPlaylistPistas().add(relacion);
                });

        // Guardar la playlist con las nuevas relaciones
        playlistRepository.save(playlist);
    }

    public List<ResponsePlaylistDTO> getKeysoundPlaylists() {
        return playlistMapper.toDtos(playlistRepository.findByPropietarioId(1L));
    }

    public ResponsePlaylistCompletaDTO getPlaylistById(Long id, String fecha) {
        if (id.equals(1L)) {
            return rankingService.getDailyTop30(fecha);
        }

        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new PlaylistNotFoundException(id));

        return playlistMapper.toDtoCompleto(playlist);
    }

    public void eliminarCancionDePlaylist(Long playlistId, Long pistaId, String token) {
        String username = jwtService.extractUsername(token);
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new PlaylistNotFoundException(playlistId));

        if (!playlist.getPropietario().getId().equals(usuario.getId())) {
            throw new OwnershipRequiredException();
        }

        PlaylistPistaId relId = new PlaylistPistaId(playlistId, pistaId);
        playlistPistaRepository.deleteById(relId);
    }

    public void eliminarPlaylist(Long id, String token) {
        String username = jwtService.extractUsername(token);
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new PlaylistNotFoundException(id));

        if (!playlist.getPropietario().getId().equals(usuario.getId())) {
            throw new OwnershipRequiredException();
        }

        playlistRepository.delete(playlist);
    }

    public List<ResponsePlaylistDTO> getMisPlaylists(String token) {
        String username = jwtService.extractUsername(token);
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return playlistMapper.toDtos(
                usuario
                        .getPlaylists()
                        .stream()
                        .toList()
        );
    }

    public List<ResponsePlaylistDTO> buscarPlaylists(String q) {
        if (q == null || q.isBlank()) return List.of();

        return playlistMapper.toDtos(playlistRepository.buscarPorNombre(q));
    }

    public void editarPlaylist(Long id, RequestPlaylistDTO dto, String token) {
        String username = jwtService.extractUsername(token);
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new PlaylistNotFoundException(id));

        if (!playlist.getPropietario().getId().equals(usuario.getId())) {
            throw new OwnershipRequiredException();
        }

        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            playlist.setNombre(dto.getNombre());
        }

        if (dto.getDescripcion() != null) {
            playlist.setDescripcion(dto.getDescripcion());
        }

        if (dto.getFotoPortada() != null && !dto.getFotoPortada().isEmpty()) {
            String nuevaFoto = firebaseService.subirArchivo(dto.getFotoPortada(),
                    "playlist_" + usuario.getId() + "_" + playlist.getNombre() + "_");
            playlist.setFotoPortada(nuevaFoto);
        }

        playlistRepository.save(playlist);
    }

    // ===== AUTOMATIZACIÓN DE PLAYLISTS DE KEYSOUND =====

    /**
     * Actualiza automáticamente la playlist "Top 30 del día" con 30 canciones aleatorias.
     * Se ejecuta todos los días a las 00:00 (medianoche).
     */
    @Scheduled(cron = "0 0 0 * * *") // Ejecutar a medianoche todos los días
    @Transactional
    public void actualizarTop30DiaProgramado() {
        log.info("Iniciando actualización automática de 'Top 30 del día'");
        try {
            Playlist playlist = playlistRepository.findByPropietarioId(1L).stream()
                    .filter(p -> p.getNombre().equalsIgnoreCase("Top 30 del día"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Playlist 'Top 30 del día' no encontrada"));
            log.info("'Top 30 del día' actualizada correctamente con {} canciones", 30);
        } catch (Exception e) {
            log.error("Error al actualizar 'Top 30 del día': {}", e.getMessage(), e);
        }
    }

    /**
     * Actualiza automáticamente la playlist "Nuestras favoritas" con las canciones más reproducidas.
     * Se ejecuta todos los días a las 01:00.
     */
    @Scheduled(cron = "0 0 1 * * *") // Ejecutar a la 1:00 AM todos los días
    @Transactional
    public void actualizarNuestrasFavoritasProgramado() {
        log.info("Iniciando actualización automática de 'Nuestras favoritas'");
        try {
            Playlist playlist = playlistRepository.findByPropietarioId(1L).stream()
                    .filter(p -> p.getNombre().equalsIgnoreCase("Nuestras favoritas"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Playlist 'Nuestras favoritas' no encontrada"));

            log.info("'Nuestras favoritas' actualizada correctamente con las canciones más reproducidas");
        } catch (Exception e) {
            log.error("Error al actualizar 'Nuestras favoritas': {}", e.getMessage(), e);
        }
    }

    /**
     * Actualiza automáticamente la playlist "Descubrir" con canciones poco reproducidas.
     * Se ejecuta todos los días a las 02:00.
     */
    @Scheduled(cron = "0 0 2 * * *") // Ejecutar a las 2:00 AM todos los días
    @Transactional
    public void actualizarDescubrirProgramado() {
        log.info("Iniciando actualización automática de 'Descubrir'");
        try {
            Playlist playlist = playlistRepository.findByPropietarioId(1L).stream()
                    .filter(p -> p.getNombre().equalsIgnoreCase("Descubrir"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Playlist 'Descubrir' no encontrada"));

            log.info("'Descubrir' actualizada correctamente con canciones para descubrir");
        } catch (Exception e) {
            log.error("Error al actualizar 'Descubrir': {}", e.getMessage(), e);
        }
    }
}
