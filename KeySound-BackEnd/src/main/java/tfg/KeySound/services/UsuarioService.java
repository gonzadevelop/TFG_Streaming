package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.entitys.*;
import tfg.KeySound.exception.artista.FollowRestrictionException;
import tfg.KeySound.exception.auth.UsernameNotFoundException;
import tfg.KeySound.exception.pista.PistaNotFoundException;
import tfg.KeySound.exception.playlist.FavoriteAlreadyExistsException;
import tfg.KeySound.exception.usuario.AlreadyFollowingException;
import tfg.KeySound.exception.usuario.SelfFollowException;
import tfg.KeySound.mappers.PlaylistMapper;
import tfg.KeySound.model.playlist.ResponsePlaylistDTO;
import tfg.KeySound.repositorys.*;
import tfg.KeySound.services.external.FirebaseService;
import tfg.KeySound.mappers.UsuarioMapper;
import tfg.KeySound.model.usuario.ResponseUsuarioDTO;
import tfg.KeySound.services.external.JwtService;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    /**
     * Inyecciones por constructor
     */
    private final FirebaseService firebaseService;
    private final JwtService jwtService;

    private final PistaRepository pistaRepository;
    private final UsuarioRepository usuarioRepository;

    private final UsuarioMapper usuarioMapper;
    private final PlaylistMapper playlistMapper;

    /**
     * Metodos llamados por endpoints
     */
    public void agregarCancionAFavoritos(Long pistaId, String token) {

        // Extraer el nombre de usuario del token JWT
        String username = jwtService.extractUsername(token);

        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Pista pista = pistaRepository.findById(pistaId)
                .orElseThrow(() -> new PistaNotFoundException(pistaId));

        // Verificar si la canción ya está en favoritos del usuario
        if (usuario.getFavoritos().contains(pista)) throw new FavoriteAlreadyExistsException();

        // Agregar la canción a favoritos del usuario
        usuario.getFavoritos().add(pista);
        usuarioRepository.save(usuario);
    }

    public ResponseUsuarioDTO obtenerInfoUsuario(String username) {
        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        // Obtener la URL del avatar del usuario
        String urlAvatar = firebaseService.obtenerUrlArchivoImagen(usuario.getArchivoAvatar(), usuario.getUsername());

        List<ResponsePlaylistDTO> playlists = Stream.concat(
                // Añadir la "playlist" de favoritos del usuario a la lista de playlists, con un nombre fijo y la portada por defecto.
                Stream.of(ResponsePlaylistDTO.builder()
                        .id(0L)
                        .nombre("Favoritos")
                        .descripcion("Canciones que " + usuario.getUsername() + " ha añadido a favoritos")
                        .urlPortada(firebaseService.obtenerUrlArchivoImagen("KeySound_Favoritos_82343704-6207-4d1d-b810-a1941cdcce78", ""))
                        .build()),

                // Añadimos las playlists del usuario a la lista de playlists.
                usuario.getPlaylists().stream()
                        .map(playlist -> {
                            String url = firebaseService.obtenerUrlArchivoImagen(playlist.getFotoPortada(), playlist.getNombre());
                            return playlistMapper.toDto(playlist, url);
                        })
        ).toList();

        // Mapear el usuario a ResponseUsuarioDTO y devolverlo
        return usuarioMapper.toDto(usuario, urlAvatar, playlists);
    }

    public void seguirUsuario(Long id, String substring) {
        // Extraer el nombre de usuario del token JWT
        String usernameSeguidor = jwtService.extractUsername(substring);

        // Buscar el usuario seguidor en la base de datos
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(usernameSeguidor)
                .orElseThrow(() -> new UsernameNotFoundException(usernameSeguidor));

        if (usuario.getRol().getNombre().equals("ROLE_ARTISTA")) throw new FollowRestrictionException();


        // Buscar el usuario que se quiere seguir en la base de datos
        Usuario usuarioASeguir = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(id));

        if (usuario.equals(usuarioASeguir)) throw new SelfFollowException();

        // Verificar si el seguidor ya sigue al usuario y si le sigue, no hacer nada
        if (usuario.getSeguidores().contains(usuarioASeguir)) throw new AlreadyFollowingException();

        // Agregar el seguidor a la lista de seguidores del usuario a seguir
        usuario.getSeguidos().add(usuarioASeguir);
        usuarioRepository.save(usuario);
    }

    public void dejarDeSeguirUsuario(Long id, String token) {
        String usernameFollower = jwtService.extractUsername(token);

        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(usernameFollower)
                .orElseThrow(() -> new UsernameNotFoundException(usernameFollower));

        Usuario usuarioADejarDeSeguir = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(id));

        usuario.getSeguidos().remove(usuarioADejarDeSeguir);
        usuarioRepository.save(usuario);
    }

    public String obtenerUsername(String token) {
        return jwtService.extractUsername(token);
    }

    public ResponseUsuarioDTO actualizarPerfil(ResponseUsuarioDTO dto, String token) {
        String username = jwtService.extractUsername(token);

        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        if (dto.getEmail() != null) usuario.setEmail(dto.getEmail());
        if (dto.getBiografia() != null) usuario.setBiografia(dto.getBiografia());

        usuarioRepository.save(usuario);
        return obtenerInfoUsuario(usuario.getUsername());
    }

    public void actualizarAvatar(MultipartFile avatar, String token) {
        String username = jwtService.extractUsername(token);
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        if (usuario.getArchivoAvatar() != null && !usuario.getArchivoAvatar().isBlank()) {
            firebaseService.borrarArchivo(usuario.getArchivoAvatar());
        }
        String nombreArchivo = firebaseService.subirArchivo(avatar, "avatar_" + username + "_");
        usuario.setArchivoAvatar(nombreArchivo);
        usuarioRepository.save(usuario);
    }
}
