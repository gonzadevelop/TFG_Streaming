package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.entitys.*;
import tfg.KeySound.exception.auth.UsernameNotFoundException;
import tfg.KeySound.exception.cancion.CancionNotFoundException;
import tfg.KeySound.mappers.PistaMapper;
import tfg.KeySound.model.cancion.ResponseCancionExistenteDTO;
import tfg.KeySound.model.pista.ResponsePistaBusquedaDTO;
import tfg.KeySound.model.pista.ResponsePistaHomeDTO;
import tfg.KeySound.repositorys.HistorialReproduccionesRepository;
import tfg.KeySound.repositorys.PistaRepository;
import tfg.KeySound.repositorys.UsuarioRepository;
import tfg.KeySound.services.external.FirebaseService;
import tfg.KeySound.services.external.JwtService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CancionService {

    /**
     * Inyecciones por constructor
     */
    private final JwtService jwtService;
    private final FirebaseService firebaseService;

    private final PistaRepository pistaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialReproduccionesRepository historialReproduccionesRepository;

    private final PistaMapper pistaMapper;

    /**
     * Metodos llamados por endpoints
     */
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

    public List<ResponsePistaHomeDTO> obtenerMisCancionesMasReproducidas(String token) {
        // Si el token está vacío, devolver una lista vacía
        if (token.isEmpty()) return List.of();

        // Extraer el nombre de usuario del token JWT
        String username = jwtService.extractUsername(token);

        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        // Obtener las 10 canciones más reproducidas del usuario
        List<Cancion> canciones = historialReproduccionesRepository.findTop10MostPlayed(usuario.getId());

        // Mapear a DTO con reproducciones específicas del usuario (el mapper maneja toda la lógica)
        return pistaMapper.toDtosConReproduccionesDelUsuario(canciones, usuario.getId());
    }

    public List<ResponsePistaBusquedaDTO> buscarCanciones(String query) {
        return pistaRepository.findByCancionTituloContainingIgnoreCase(query)
                .stream()
                .map(p -> new ResponsePistaBusquedaDTO(
                        p.getId(),
                        p.getCancion().getTitulo(),
                        p.getAlbum().getUsuario().getUsername(),
                        p.getAlbum().getTitulo(),
                        firebaseService.obtenerUrlArchivoImagen(
                                p.getAlbum().getArchivoPortada(),
                                p.getAlbum().getTitulo()
                        )
                ))
                .toList();
    }       // TODO: mejorar este endpoint (añadir mapeo con la clase correspondiente)

    public List<ResponseCancionExistenteDTO> buscarMisCanciones(String token, String q) {
        if (q == null || q.isBlank()) return List.of();

        String username = jwtService.extractUsername(token);
        Usuario artista = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return pistaRepository.buscarCancionesDeArtista(artista.getId(), q)
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        p -> p.getCancion().getId(),
                        p -> new ResponseCancionExistenteDTO(
                                p.getCancion().getId(),
                                p.getCancion().getTitulo(),
                                p.getAlbum().getTitulo(),
                                firebaseService.obtenerUrlArchivoImagen(
                                        p.getAlbum().getArchivoPortada(),
                                        p.getAlbum().getTitulo()
                                )
                        ),
                        (first, ignored) -> first,
                        java.util.LinkedHashMap::new
                ))
                .values()
                .stream()
                .toList();
    }
}
