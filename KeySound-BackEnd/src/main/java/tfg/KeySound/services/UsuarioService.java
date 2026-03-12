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
