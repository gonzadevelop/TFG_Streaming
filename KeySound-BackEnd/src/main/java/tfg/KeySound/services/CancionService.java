package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.entitys.Cancion;
import tfg.KeySound.entitys.HistorialReproducciones;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.exception.auth.UsernameNotFoundException;
import tfg.KeySound.exception.cancion.CancionNotFoundException;
import tfg.KeySound.mappers.ArtistaMapper;
import tfg.KeySound.mappers.PistaMapper;
import tfg.KeySound.model.pista.ResponsePistaHomeDTO;
import tfg.KeySound.repositorys.CancionRepository;
import tfg.KeySound.repositorys.HistorialReproduccionesRepository;
import tfg.KeySound.repositorys.PistaRepository;
import tfg.KeySound.repositorys.UsuarioRepository;
import tfg.KeySound.services.external.FirebaseService;
import tfg.KeySound.services.external.JwtService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class CancionService {

    /**
     * Inyecciones por constructor
     */
    private final JwtService jwtService;
    private final FirebaseService firebaseService;

    private final CancionRepository cancionRepository;
    private final PistaRepository pistaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialReproduccionesRepository historialReproduccionesRepository;

    private final ArtistaMapper artistaMapper;
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
        // Extraer el nombre de usuario del token JWT
        String username = jwtService.extractUsername(token);

        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        List<Cancion> canciones = historialReproduccionesRepository.findTop10ByUsuarioOrderByFechaReproduccionDesc(usuario.getId());

        List<ResponsePistaHomeDTO> pistaDto = pistaMapper.toDtos(canciones);

        for (int i = 0; i < pistaDto.size(); i++) {
            pistaDto.get(i).setUrlPortada(firebaseService.obtenerUrlArchivoImagen(
                    canciones.get(i).getPistas().stream().findFirst().get().getAlbum().getArchivoPortada(),
                    canciones.get(i).getPistas().stream().findFirst().get().getAlbum().getTitulo())
            );

            pistaDto.get(i).setArtistas(artistaMapper.toMiniDtos(obtenerArtistasDeCancion(pistaDto.get(i).getCancionId())));

            pistaDto.get(i)
                    .setAlbumId(
                            canciones.get(i).getPistas().stream().findFirst().get().getAlbum().getId()
                    );
        }

        return pistaDto;
    }

    /**
     * Métodos auxiliares
     */
    public List<Usuario> obtenerArtistasDeCancion(Long cancionId) {
        Cancion cancion = cancionRepository.findById(cancionId)
                .orElseThrow(() -> new CancionNotFoundException(cancionId));

        Usuario artistaPrincipal = cancion.getPistas()
                .stream()
                .findFirst()
                .get()
                .getAlbum()
                .getUsuario();

        return Stream.concat(
                Stream.of(
                        artistaPrincipal
                ),
                cancion
                        .getUsuarios()
                        .stream()
                        .filter(artista -> !artista.getId().equals(artistaPrincipal.getId()))
        ).toList();
    }
}
