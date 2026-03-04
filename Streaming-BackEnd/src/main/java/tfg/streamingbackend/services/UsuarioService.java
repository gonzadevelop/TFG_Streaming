package tfg.streamingbackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.streamingbackend.entitys.Cancion;
import tfg.streamingbackend.entitys.Playlist;
import tfg.streamingbackend.entitys.PlaylistLanzamientoCancion;
import tfg.streamingbackend.entitys.Usuario;
import tfg.streamingbackend.entitys.embeddedids.PlaylistLanzamientoCancionId;
import tfg.streamingbackend.exception.auth.UsernameNotFoundException;
import tfg.streamingbackend.exception.cancion.CancionNotFoundException;
import tfg.streamingbackend.exception.cancion.FileUploadException;
import tfg.streamingbackend.exception.lanzamiento.LanzamientoCancionNotFoundException;
import tfg.streamingbackend.exception.playlist.OwnershipRequiredException;
import tfg.streamingbackend.exception.playlist.PlaylistNotFoundException;
import tfg.streamingbackend.firebase.FirebaseService;
import tfg.streamingbackend.mappers.PlaylistMapper;
import tfg.streamingbackend.model.AddCancionesPlaylistDTO;
import tfg.streamingbackend.model.CrearPlaylistDTO;
import tfg.streamingbackend.repositorys.CancionRepository;
import tfg.streamingbackend.repositorys.LanzamientoCancionRepository;
import tfg.streamingbackend.repositorys.PlaylistLanzamientoCancionRepository;
import tfg.streamingbackend.repositorys.PlaylistRepository;
import tfg.streamingbackend.repositorys.UsuarioRepository;
import tfg.streamingbackend.security.JwtService;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final CancionRepository cancionRepository;
    private final FirebaseService firebaseService;
    private final JwtService jwtService;
    private final LanzamientoCancionRepository lanzamientoCancionRepository;
    private final PlaylistLanzamientoCancionRepository playlistLanzamientoCancionRepository;
    private final PlaylistMapper playlistMapper;
    private final PlaylistRepository playlistRepository;
    private final UsuarioRepository usuarioRepository;


    public String obtenerUrlCancion(Long cancionId) {
        // Verificar que la canción existe
        Cancion cancion = cancionRepository.findById(cancionId)
                .orElseThrow(() -> new CancionNotFoundException(cancionId));

        // Obtener la URL pública de Firebase Storage para reproducir la canción
        return firebaseService.obtenerUrlArchivo(cancion.getArchivoCancion());
    }

    public void crearPlaylist(CrearPlaylistDTO dto, String token) {

        // Extraer el nombre de usuario del token JWT
        String username = jwtService.extractUsername(token);

        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        // Subir la foto de portada a Firebase Storage y obtener el nombre del archivo
        String nombreArchivo = null;
        if (dto.getFotoPortada() != null) {
            nombreArchivo = "playlists_" + usuario.getId() + "_" + dto.getNombrePlaylist() + "_" + UUID.randomUUID();
            try {
                firebaseService.subirArchivo(dto.getFotoPortada() , nombreArchivo);
            } catch (IOException e) {
                // Manejar la excepción si ocurre un error al cerrar el InputStream
                throw new FileUploadException();
            }
        }

        // Mapear el DTO a la entidad Playlist y establecer el propietario
        Playlist playlist = playlistMapper.toEntity(dto, usuario, nombreArchivo);

        // Guardar la nueva playlist en la base de datos
        playlistRepository.save(playlist);
    }

    public void agregarCancionesAPlaylist(AddCancionesPlaylistDTO dto, String token) {
        // Extraer el nombre de usuario del token JWT
        String username = jwtService.extractUsername(token);

        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        // Buscar la playlist por su ID y verificar que pertenece al usuario
        Playlist playlist = playlistRepository.findById(dto.getPlaylistId())
                .orElseThrow(() -> new PlaylistNotFoundException(dto.getPlaylistId()));

        if (!playlist.getPropietario().getId().equals(usuario.getId())) {
            throw new OwnershipRequiredException();
        }


        dto.getLanzamientoCancionIds().stream()
                .map(id -> lanzamientoCancionRepository.findById(id) // Buscar el lanzamiento de canción por su ID
                        .orElseThrow(() -> new LanzamientoCancionNotFoundException(id)))
                .filter(lanzamientoCancion -> !playlistLanzamientoCancionRepository // Evitar agregar canciones duplicadas a la playlist
                        .existsByPlaylistIdAndLanzamientoCancionId(playlist.getId(), lanzamientoCancion.getId()))
                .map(lanzamientoCancion -> { // Crear la relación entre la playlist y el lanzamiento de canción
                    PlaylistLanzamientoCancion relacion = new PlaylistLanzamientoCancion();
                    relacion.setId(new PlaylistLanzamientoCancionId()); // El ID se generará automáticamente al guardar la entidad
                    relacion.setPlaylist(playlist);
                    relacion.setLanzamientoCancion(lanzamientoCancion);
                    return relacion;
                })
                .forEach(playlistLanzamientoCancionRepository::save); // Guardar la relación en la base de datos
    }
}
