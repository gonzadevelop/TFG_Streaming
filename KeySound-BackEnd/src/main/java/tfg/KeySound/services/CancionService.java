package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.entitys.Cancion;
import tfg.KeySound.entitys.HistorialReproducciones;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.exception.auth.UsernameNotFoundException;
import tfg.KeySound.exception.cancion.CancionNotFoundException;
import tfg.KeySound.repositorys.HistorialReproduccionesRepository;
import tfg.KeySound.repositorys.LanzamientoCancionRepository;
import tfg.KeySound.repositorys.UsuarioRepository;
import tfg.KeySound.services.external.JwtService;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class CancionService {

    /**
     * Inyecciones por constructor
     */
    private final JwtService jwtService;

    private final LanzamientoCancionRepository lanzamientoCancionRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialReproduccionesRepository historialReproduccionesRepository;

    public void reproducir(Long lanzamientoCancionId, String substring) {
        // Extraer el nombre de usuario del token JWT
        String username = jwtService.extractUsername(substring);

        // Buscar el usuario y la cancion en la base de datos
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Cancion cancion = lanzamientoCancionRepository.findById(lanzamientoCancionId)
                .orElseThrow(() -> new CancionNotFoundException(lanzamientoCancionId))
                .getCancion();

        // Crear una nueva entrada en el historial de reproducciones
        HistorialReproducciones reproduccion = new HistorialReproducciones();
        reproduccion.setUsuario(usuario);
        reproduccion.setCancion(cancion);
        reproduccion.setFechaReproduccion(LocalDateTime.now());

        // Guardar la reproducción en la base de datos (id autogenerado)
        historialReproduccionesRepository.save(reproduccion);
    }
}
