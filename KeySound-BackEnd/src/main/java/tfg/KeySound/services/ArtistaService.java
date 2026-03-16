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
import tfg.KeySound.model.usuario.ResponseArtistaDTO;
import tfg.KeySound.repositorys.UsuarioRepository;
import tfg.KeySound.services.external.JwtService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ArtistaService {

    /**
     * Inyecciones por constructor
     */
    private final JwtService jwtService;

    private final UsuarioRepository usuarioRepository;

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
        List<Lanzamiento> lanzamientos = artista.getLanzamientos()
                .stream()
                .toList();

        // Buscar las 10 canciones más populares del artista, ordenadas por número de reproducciones (historialReproducciones)
        List<Pista> cancionesPopulares = artista.getCanciones()
                .stream()
                .sorted((c1, c2) -> Integer.compare(
                        c2.getHistorialReproducciones() != null ? c2.getHistorialReproducciones().size() : 0,
                        c1.getHistorialReproducciones() != null ? c1.getHistorialReproducciones().size() : 0))
                .limit(10)
                .map(cancion -> cancion.getPistas().stream().findFirst().orElse(null))
                .filter(Objects::nonNull)
                .toList();

        // Mapear a DTO
        List<ResponseLanzamientoArtistaDTO> lanzamientosDTO = lanzamientoMapper.toDtos(lanzamientos);
        List<ResponseCancionArtistaDTO> cancionesPopularesDTO = cancionMapper.toDtos(cancionesPopulares);
        return artistaMapper.toDto(artista, cancionesPopularesDTO, lanzamientosDTO, cancionesEnFavoritos);
    }
}
