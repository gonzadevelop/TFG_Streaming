package tfg.KeySound.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tfg.KeySound.entitys.Album;
import tfg.KeySound.entitys.Usuario;
import tfg.KeySound.exception.auth.UsernameNotFoundException;
import tfg.KeySound.mappers.ArtistaMapper;
import tfg.KeySound.mappers.AlbumMapper;
import tfg.KeySound.mappers.PistaMapper;
import tfg.KeySound.model.artista.ResponseArtistaHomeDTO;
import tfg.KeySound.model.album.ResponseAlbumDTO;
import tfg.KeySound.model.album.ResponseMiAlbumDTO;
import tfg.KeySound.model.artista.ResponseArtistaDTO;
import tfg.KeySound.model.pista.ResponsePistaHomeDTO;
import tfg.KeySound.repositorys.AlbumRepository;
import tfg.KeySound.repositorys.CancionRepository;
import tfg.KeySound.repositorys.UsuarioRepository;
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

    private final UsuarioRepository usuarioRepository;
    private final AlbumRepository albumRepository;
    private final CancionRepository cancionRepository;

    private final AlbumMapper albumMapper;
    private final ArtistaMapper artistaMapper;
    private final PistaMapper pistaMapper;

    /**
     * Metodos llamados por endpoints
     */
    public ResponseArtistaDTO obtenerInfoArtista(String username, String token) {
        // Buscar el artista por username
        Usuario artista = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        // Buscar el usuario que hace la peticion en la bd (si viene el header) para contar cuántas canciones del artista tiene en favoritos
        int cancionesEnFavoritos = 0;
        boolean sigueAlArtista = false;
        if (!token.isEmpty()) {
            String usernameToken = jwtService.extractUsername(token);
            Usuario usuarioToken = usuarioRepository.findByUsernameIgnoreCase(usernameToken)
                    .orElseThrow(() -> new UsernameNotFoundException(usernameToken));

            cancionesEnFavoritos = usuarioRepository.countFavoritosByUsuarioAndArtista(usuarioToken.getId(), artista.getId());
            sigueAlArtista = usuarioToken.getSeguidos() != null && usuarioToken.getSeguidos().contains(artista);
        }

        // Sacar todos los albums del artista.
        List<Album> albums = albumRepository.findAlbumsPublicadosPorArtista(artista.getId(), LocalDateTime.now());

        // Buscar las 10 canciones más populares del artista, ordenadas por número de reproducciones (historialReproducciones)
        List<ResponsePistaHomeDTO> cancionesPopulares = pistaMapper.toDtos(
                cancionRepository.findTop10CancionesMasReproducidasPorArtista(artista.getId())
        );

        // Mapear a DTO
        List<ResponseAlbumDTO> albumsDTO = albumMapper.toDtos(albums);

        return artistaMapper.toDto(artista, cancionesPopulares, albumsDTO, cancionesEnFavoritos);
    }

    public List<ResponseMiAlbumDTO> obtenerMisAlbums(String substring) {
        // obtener el artista a partir del token JWT
        Usuario artista = usuarioRepository.findByUsernameIgnoreCase(jwtService.extractUsername(substring))
                .orElseThrow(() -> new UsernameNotFoundException(jwtService.extractUsername(substring)));

        // sacar todos los albums del artista, ordenados por fecha de creación (los más recientes primero)
        List<Album> albums = albumRepository.findAllByArtistaOrderByFechaLanzamientoDesc(artista.getId());

        // mapear a DTO (el mapper ahora obtiene las URLs desde Firebase)
        return albumMapper.toMisAlbumsDtos(albums);
    }

    public void publicarAlbum(Long idAlbum, String token) {
        // obtener el artista a partir del JWT
        Usuario artista = usuarioRepository.findByUsernameIgnoreCase(jwtService.extractUsername(token))
                .orElseThrow(() -> new UsernameNotFoundException(jwtService.extractUsername(token)));

        // buscar el album por ID y comprobar que pertenece al artista
        Album album = albumRepository.findById(idAlbum)
                .orElseThrow(() -> new RuntimeException("Album no encontrado")); // TODO: crear excepción personalizada

        if (!album.getUsuario().getId().equals(artista.getId()))
            throw new RuntimeException("El album no pertenece al artista autenticado"); // TODO: crear excepción personalizada

        // comprobar que el album tiene fecha de lanzamiento
        if (album.getFechaLanzamiento() == null)
            throw new RuntimeException("El album debe tener fecha de lanzamiento para ser publicado"); // TODO: crear excepción personalizada

        // comprobar que la fecha de album no es anterior a hoy
        if (album.getFechaLanzamiento().isBefore(LocalDateTime.now()) || album.getFechaLanzamiento().equals(LocalDateTime.now()))
            throw new RuntimeException("La fecha de album debe ser posterior a hoy para ser publicado"); // TODO: crear excepción personalizada

        // publicar el album
        album.setEsBorrador(false);
        albumRepository.save(album);
    }

    public List<ResponseArtistaHomeDTO> obtenerArtistasQueSigo(String token) {
        // si el token es vacío, devolver una lista vacía
        if (token.isEmpty()) return List.of();

        // obtener el usuario a partir del JWT
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(jwtService.extractUsername(token))
                .orElseThrow(() -> new UsernameNotFoundException(jwtService.extractUsername(token)));

        // sacar los artistas que sigue el usuario (el repository ya filtra por rol ROLE_ARTISTA)


        // mapear a DTO
        return artistaMapper.toHomeDtos(usuarioRepository.findArtistasQueSigue(usuario.getId()));
    }

    public List<ResponseArtistaHomeDTO> buscarArtistas(String q) {
        return q.isBlank() ?
                List.of() :
                artistaMapper.toHomeDtos(usuarioRepository.buscarPorUsername(q));
    }
}
