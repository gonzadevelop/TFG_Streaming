package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.entitys.*;
import tfg.KeySound.entitys.embeddedids.PlaylistPistaId;
import tfg.KeySound.exception.auth.UsernameNotFoundException;
import tfg.KeySound.exception.lanzamiento.PistaNotFoundException;
import tfg.KeySound.exception.playlist.OwnershipRequiredException;
import tfg.KeySound.exception.playlist.PlaylistNotFoundException;
import tfg.KeySound.mappers.PlaylistMapper;
import tfg.KeySound.model.cancion.RequestCancionesPlaylistDTO;
import tfg.KeySound.model.playlist.RequestPlaylistDTO;
import tfg.KeySound.repositorys.*;
import tfg.KeySound.services.external.FirebaseService;
import tfg.KeySound.services.external.JwtService;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    /**
     * Inyecciones por constructor
     */
    private final FirebaseService firebaseService;
    private final JwtService jwtService;
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

        // Subir la foto de portada a Firebase Storage y obtener el nombre del archivo o usar una imagen por defecto si no se proporciona una foto de portada
        String nombreArchivo = dto.getFotoPortada() != null ?
                firebaseService.subirArchivo(dto.getFotoPortada() , "playlist_" + usuario.getId() + "_" + dto.getNombrePlaylist() + "_")
                : "";

        // Mapear el DTO a la entidad Playlist y establecer el propietario
        Playlist playlist = playlistMapper.toEntity(dto, usuario, nombreArchivo);

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

        dto.getPistaIds().stream()
                .map(id -> pistaRepository.findById(id) // Buscar el lanzamiento de canción por su ID
                        .orElseThrow(() -> new PistaNotFoundException(id)))
                .filter(pista -> !playlistPistaRepository // Evitar agregar canciones duplicadas a la playlist
                        .existsByPlaylistIdAndPistaId(playlist.getId(), pista.getId()))
                .map(pista -> { // Crear la relación entre la playlist y el lanzamiento de canción
                    PlaylistPista relacion = new PlaylistPista();
                    relacion.setId(new PlaylistPistaId()); // El ID se generará automáticamente al guardar la entidad
                    relacion.setPlaylist(playlist);
                    relacion.setPista(pista);
                    return relacion;
                })
                .forEach(playlistPistaRepository::save); // Guardar la relación en la base de datos
    }
}
