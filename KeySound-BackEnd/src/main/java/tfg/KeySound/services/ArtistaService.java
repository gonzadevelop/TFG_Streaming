package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.entitys.Lanzamiento;
import tfg.KeySound.entitys.Pista;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.exception.auth.UsernameNotFoundException;
import tfg.KeySound.mappers.ArtistaMapper;
import tfg.KeySound.mappers.CancionMapper;
import tfg.KeySound.mappers.LanzamientoMapper;
import tfg.KeySound.model.cancion.ResponseCancionArtistaDTO;
import tfg.KeySound.model.lanzamiento.ResponseLanzamientoArtistaDTO;
import tfg.KeySound.model.lanzamiento.ResponseMiLanzamientoDTO;
import tfg.KeySound.model.usuario.ResponseArtistaDTO;
import tfg.KeySound.repositorys.LanzamientoRepository;
import tfg.KeySound.repositorys.UsuarioRepository;
import tfg.KeySound.services.external.FirebaseService;
import tfg.KeySound.services.external.JwtService;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ArtistaService {

    /**
     * Inyecciones por constructor
     */
    private final JwtService jwtService;
    private final FirebaseService firebaseService;

    private final UsuarioRepository usuarioRepository;
    private final LanzamientoRepository lanzamientoRepository;

    private final CancionMapper cancionMapper;
    private final LanzamientoMapper lanzamientoMapper;
    private final ArtistaMapper artistaMapper;

    /**
     * Metodos llamados por endpoints
     */
    public ResponseArtistaDTO obtenerInfoArtista(String username, String token) {
        // Buscar el artista por username
        Usuario artista = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));


        // Buscar el usuario que hace la peticion en la bd (si viene el header) para contar cuántas canciones del artista tiene en favoritos
        int cancionesEnFavoritos = 0;
        if (!token.isEmpty()) {
            String usernameToken = jwtService.extractUsername(token);
            Usuario usuarioToken = usuarioRepository.findByUsernameIgnoreCase(usernameToken)
                    .orElseThrow(() -> new UsernameNotFoundException(usernameToken));

            cancionesEnFavoritos = usuarioToken
                    .getFavoritos()
                    .stream()
                    .filter(pista -> pista.getCancion().getUsuarios().contains(artista))
                    .mapToInt(pista -> 1)
                    .sum();
        }

        // Sacar todos los lanzamientos del artista.
        List<Lanzamiento> lanzamientos = artista
                .getLanzamientos()
                .stream()
                .filter(l -> !l.getEsBorrador()
                        && l.getFechaLanzamiento().isBefore(LocalDateTime.now())) // Filtrar los lanzamientos que no son borradores y con fecha de lanzamiento anterior a hoy
                .sorted(Comparator.comparing(Lanzamiento::getFechaLanzamiento).reversed())
                .toList();

        // Buscar las 10 canciones más populares del artista, ordenadas por número de reproducciones (historialReproducciones)
        List<Pista> cancionesPopulares = artista.getCanciones()
                .stream()
                .sorted((c1, c2) -> Integer.compare(
                        c2.getHistorialReproducciones() != null ? c2.getHistorialReproducciones().size() : 0,
                        c1.getHistorialReproducciones() != null ? c1.getHistorialReproducciones().size() : 0))
                .limit(10)
                .map(cancion -> cancion.getPistas().stream().findFirst())
                .flatMap(Optional::stream)
                .toList();

        // Obtener la URL del avatar del artista desde Firebase o de ui-avatars si no tiene avatar
        String urlAvatar = firebaseService.obtenerUrlArchivoImagen(artista.getArchivoAvatar(), artista.getUsername());

        // Mapear a DTO
        List<ResponseLanzamientoArtistaDTO> lanzamientosDTO = lanzamientoMapper.toDtos(lanzamientos);
        List<ResponseCancionArtistaDTO> cancionesPopularesDTO = cancionMapper.toDtos(cancionesPopulares);
        return artistaMapper.toDto(artista, cancionesPopularesDTO, lanzamientosDTO, cancionesEnFavoritos, urlAvatar);
    }

    public List<ResponseMiLanzamientoDTO> obtenerMisLanzamientos(String substring) {
        // obtener el artista a partir del token JWT
        Usuario artista = usuarioRepository.findByUsernameIgnoreCase(jwtService.extractUsername(substring))
                .orElseThrow(() -> new UsernameNotFoundException(jwtService.extractUsername(substring)));

        // sacar todos los lanzamientos del artista, ordenados por fecha de creación (los más recientes primero)
        List<Lanzamiento> lanzamientos = artista
                .getLanzamientos()
                .stream()
                .sorted(Comparator.comparing(Lanzamiento::getFechaLanzamiento).reversed())
                .toList();

        // mapear a DTO
        List<ResponseMiLanzamientoDTO> lanzamientosDTO = lanzamientoMapper.toMisLanzamientosDtos(lanzamientos);

        // obtener la URL de la portada de cada lanzamiento desde Firebase o de ui-avatars si no tiene portada
        for (int i = 0; i < lanzamientos.size(); i++) {
            String urlPortada = firebaseService.obtenerUrlArchivoImagen(lanzamientos.get(i).getArchivoPortada(), lanzamientos.get(i).getTitulo());
            lanzamientosDTO.get(i).setPortada(urlPortada);
        }

        return lanzamientosDTO;
    }

    public void publicarLanzamiento(Long idLanzamiento, String token) {
        // obtener el artista a partir del JWT
        Usuario artista = usuarioRepository.findByUsernameIgnoreCase(jwtService.extractUsername(token))
                .orElseThrow(() -> new UsernameNotFoundException(jwtService.extractUsername(token)));

        // buscar el lanzamiento por ID y comprobar que pertenece al artista
        Lanzamiento lanzamiento = artista.getLanzamientos()
                .stream()
                .filter(l -> l.getId().equals(idLanzamiento))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Lanzamiento no encontrado o no pertenece al artista"));

        // comprobar que el lanzamiento tiene fecha de lanzamiento y portada
        if (lanzamiento.getFechaLanzamiento() == null) throw new RuntimeException("El lanzamiento debe tener fecha de lanzamiento y portada para ser publicado");

        // comprobar que la fecha de lanzamiento no es anterior a hoy
        if (lanzamiento.getFechaLanzamiento().isBefore(LocalDateTime.now()) || lanzamiento.getFechaLanzamiento().equals(LocalDateTime.now()))
            throw new RuntimeException("La fecha de lanzamiento debe ser posterior a hoy para ser publicado");

        // publicar el lanzamiento
        lanzamiento.setEsBorrador(false);
        lanzamientoRepository.save(lanzamiento);
    }
}
