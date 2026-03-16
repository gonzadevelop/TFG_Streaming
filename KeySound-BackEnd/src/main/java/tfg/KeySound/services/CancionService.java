package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.entitys.Cancion;
import tfg.KeySound.entitys.HistorialReproducciones;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.exception.auth.UsernameNotFoundException;
import tfg.KeySound.exception.cancion.CancionNotFoundException;
import tfg.KeySound.repositorys.HistorialReproduccionesRepository;
import tfg.KeySound.repositorys.PistaRepository;
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

    private final PistaRepository pistaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialReproduccionesRepository historialReproduccionesRepository;

    public void reproducir(Long pistaId, String substring) {
        // Extraer el nombre de usuario del token JWT
        String username = jwtService.extractUsername(substring);

        // Buscar el usuario y la cancion en la base de datos
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Cancion cancion = pistaRepository.findById(pistaId)
                .orElseThrow(() -> new CancionNotFoundException(pistaId))
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
