package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.entitys.LanzamientoCancion;
import tfg.KeySound.entitys.Playlist;
import tfg.KeySound.entitys.PlaylistLanzamientoCancion;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.entitys.embeddedids.PlaylistLanzamientoCancionId;
import tfg.KeySound.exception.auth.UsernameNotFoundException;
import tfg.KeySound.exception.lanzamiento.LanzamientoCancionNotFoundException;
import tfg.KeySound.exception.playlist.FavoriteAlreadyExistsException;
import tfg.KeySound.exception.playlist.OwnershipRequiredException;
import tfg.KeySound.exception.playlist.PlaylistNotFoundException;
import tfg.KeySound.services.external.FirebaseService;
import tfg.KeySound.mappers.PlaylistMapper;
import tfg.KeySound.mappers.UsuarioMapper;
import tfg.KeySound.model.cancion.RequestCancionesPlaylistDTO;
import tfg.KeySound.model.playlist.RequestPlaylistDTO;
import tfg.KeySound.model.usuario.ResponseUsuarioDTO;
import tfg.KeySound.repositorys.LanzamientoCancionRepository;
import tfg.KeySound.repositorys.PlaylistLanzamientoCancionRepository;
import tfg.KeySound.repositorys.PlaylistRepository;
import tfg.KeySound.repositorys.UsuarioRepository;
import tfg.KeySound.services.external.JwtService;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    // --------------- INYECCIONES POR CONSTRUCTOR ---------------
    private final FirebaseService firebaseService;
    private final JwtService jwtService;
    private final LanzamientoCancionRepository lanzamientoCancionRepository;

    private final PlaylistLanzamientoCancionRepository playlistLanzamientoCancionRepository;
    private final PlaylistRepository playlistRepository;

    private final PlaylistMapper playlistMapper;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    // -------------- MÉTODOS LLAMADOS POR ENDPOINTS --------------

    public void crearPlaylist(RequestPlaylistDTO dto, String token) {

        // Extraer el nombre de usuario del token JWT
        String username = jwtService.extractUsername(token);

        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        // Subir la foto de portada a Firebase Storage y obtener el nombre del archivo
        String nombreArchivo = null;
        if (dto.getFotoPortada() != null) {
            String nombre = "playlists_" + usuario.getId() + "_" + dto.getNombrePlaylist();

            nombreArchivo = firebaseService.subirArchivo(dto.getFotoPortada() , nombre);
        }

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

    public void agregarCancionAFavoritos(Long lanzamientoCancionId, String token) {

        // Extraer el nombre de usuario del token JWT
        String username = jwtService.extractUsername(token);

        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        LanzamientoCancion lanzamiento = lanzamientoCancionRepository.findById(lanzamientoCancionId)
                .orElseThrow(() -> new LanzamientoCancionNotFoundException(lanzamientoCancionId));

        // Verificar si la canción ya está en favoritos del usuario
        if (usuario.getFavoritos().contains(lanzamiento)) {
            throw new FavoriteAlreadyExistsException();
        }

        // Agregar la canción a favoritos del usuario
        usuario.getFavoritos().add(lanzamiento);
        usuarioRepository.save(usuario);
    }

    // más adelante añadir playlists de usuario, canciones favoritas, etc.
    public ResponseUsuarioDTO obtenerInfoUsuario(String username) {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        String urlAvatar = usuario.getArchivoAvatar() != null ?
                firebaseService.obtenerUrlArchivo(usuario.getArchivoAvatar()) :
                "https://ui-avatars.com/api/?name=" + usuario.getUsername().charAt(0) + "&background=0b75c0&bold=true&color=FFF&size=256";

        return usuarioMapper.toDto(usuario, urlAvatar);
    }
}
