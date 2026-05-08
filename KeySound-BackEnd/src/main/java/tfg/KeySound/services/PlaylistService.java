package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import tfg.KeySound.services.external.JwtService;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    /**
     * Inyecciones por constructor
     */
    private final JwtService jwtService;
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
        List<PlaylistPista> nuevasRelaciones = pistasNuevas.stream()
                .filter(pista -> !pistasExistentesIds.contains(pista.getId()))
                .map(pista -> {
                    PlaylistPista relacion = new PlaylistPista();
                    relacion.setId(new PlaylistPistaId(playlist.getId(), pista.getId()));
                    relacion.setPlaylist(playlist);
                    relacion.setPista(pista);
                    return relacion;
                })
                .toList();

        // Guardar las nuevas relaciones en la base de datos
        playlistPistaRepository.saveAll(nuevasRelaciones);
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

    public void eliminarPlaylist(Long id, String token) {
        String username = jwtService.extractUsername(token);
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new PlaylistNotFoundException(id));

        if (!playlist.getPropietario().getId().equals(usuario.getId())) {
            throw new OwnershipRequiredException();
        }

        playlistPistaRepository.deleteAll(playlist.getPlaylistPistas());
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
}
