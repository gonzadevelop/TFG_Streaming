package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.entitys.*;
import tfg.KeySound.exception.auth.UsernameNotFoundException;
import tfg.KeySound.exception.lanzamiento.LanzamientoCancionNotFoundException;
import tfg.KeySound.exception.playlist.FavoriteAlreadyExistsException;
import tfg.KeySound.repositorys.*;
import tfg.KeySound.services.external.FirebaseService;
import tfg.KeySound.mappers.UsuarioMapper;
import tfg.KeySound.model.usuario.ResponseUsuarioDTO;
import tfg.KeySound.services.external.JwtService;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    /**
     * Inyecciones por constructor
     */
    private final FirebaseService firebaseService;
    private final JwtService jwtService;

    private final LanzamientoCancionRepository lanzamientoCancionRepository;
    private final UsuarioRepository usuarioRepository;

    private final UsuarioMapper usuarioMapper;

    /**
     * Metodos llamados por endpoints
     */
    public void agregarCancionAFavoritos(Long lanzamientoCancionId, String token) {

        // Extraer el nombre de usuario del token JWT
        String username = jwtService.extractUsername(token);

        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        LanzamientoCancion lanzamiento = lanzamientoCancionRepository.findById(lanzamientoCancionId)
                .orElseThrow(() -> new LanzamientoCancionNotFoundException(lanzamientoCancionId));

        // Verificar si la canción ya está en favoritos del usuario
        if (usuario.getFavoritos().contains(lanzamiento)) throw new FavoriteAlreadyExistsException();

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

    public void seguirUsuario(String username, String substring) {
        // Extraer el nombre de usuario del token JWT
        String usernameSeguidor = jwtService.extractUsername(substring);

        // Buscar el usuario seguidor en la base de datos
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(usernameSeguidor)
                .orElseThrow(() -> new UsernameNotFoundException(usernameSeguidor));

        if (usuario.getRol().getNombre().equals("ROLE_ARTISTA")) throw new RuntimeException("Los artistas no pueden seguir a otros usuarios");


        // Buscar el usuario que se quiere seguir en la base de datos
        Usuario usuarioASeguir = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        if (usuario.equals(usuarioASeguir)) throw new RuntimeException("No puedes seguirte a ti mismo");

        // Verificar si el seguidor ya sigue al usuario y si le sigue, no hacer nada
        if (usuario.getSeguidores().contains(usuarioASeguir)) throw new RuntimeException("Ya sigues a este usuario");

        // Agregar el seguidor a la lista de seguidores del usuario a seguir
        usuario.getSeguidores().add(usuarioASeguir);
        usuarioRepository.save(usuarioASeguir);
    }
}
