package tfg.streamingbackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.streamingbackend.entitys.Cancion;
import tfg.streamingbackend.entitys.Playlist;
import tfg.streamingbackend.entitys.Usuario;
import tfg.streamingbackend.exception.auth.UsernameNotFoundException;
import tfg.streamingbackend.exception.cancion.CancionNotFoundException;
import tfg.streamingbackend.exception.cancion.FileUploadException;
import tfg.streamingbackend.firebase.FirebaseService;
import tfg.streamingbackend.mappers.PlaylistMapper;
import tfg.streamingbackend.model.CrearPlaylistDTO;
import tfg.streamingbackend.repositorys.CancionRepository;
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
    private final PlaylistRepository playlistRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlaylistMapper playlistMapper;


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
}
